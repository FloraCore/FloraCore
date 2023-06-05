package team.floracore.lib.jarrelocator;

import team.floracore.lib.asm.ClassReader;
import team.floracore.lib.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.*;
import java.util.regex.Pattern;

/**
 * A task that copies {@link JarEntry jar entries} from a {@link JarFile jar input} to a
 * {@link JarOutputStream jar output}, applying the relocations defined by a
 * {@link RelocatingRemapper}.
 */
final class JarRelocatorTask {

    /**
     * META-INF/*.SF
     * META-INF/*.DSA
     * META-INF/*.RSA
     * META-INF/SIG-*
     *
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#signed-jar-file">Specification</a>
     */
    private static final Pattern SIGNATURE_FILE_PATTERN = Pattern.compile(
            "META-INF/(?:[^/]+\\.(?:DSA|RSA|SF)|SIG-[^/]+)");

    /**
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#signature-validation">Specification</a>
     */
    private static final Pattern SIGNATURE_PROPERTY_PATTERN = Pattern.compile(".*-Digest");

    private final RelocatingRemapper remapper;
    private final JarOutputStream jarOut;
    private final JarFile jarIn;

    private final Set<String> resources = new HashSet<>();

    JarRelocatorTask(RelocatingRemapper remapper, JarOutputStream jarOut, JarFile jarIn) {
        this.remapper = remapper;
        this.jarOut = jarOut;
        this.jarIn = jarIn;
    }

    private static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[8192];
        while (true) {
            int n = from.read(buf);
            if (n == -1) {
                break;
            }
            to.write(buf, 0, n);
        }
    }

    void processEntries() throws IOException {
        for (Enumeration<JarEntry> entries = this.jarIn.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();

            // The 'INDEX.LIST' file is an optional file, containing information about the packages
            // defined in a jar. Instead of relocating the entries in it, we delete it, since it is
            // optional anyway.
            //
            // We don't process directory entries, and instead opt to recreate them when adding
            // classes/resources.
            String name = entry.getName();
            if (name.equals("META-INF/INDEX.LIST") || entry.isDirectory()) {
                continue;
            }

            // Signatures will become invalid after remapping, so we delete them to avoid making the output useless
            if (SIGNATURE_FILE_PATTERN.matcher(name).matches()) {
                continue;
            }

            try (InputStream entryIn = this.jarIn.getInputStream(entry)) {
                processEntry(entry, entryIn);
            }
        }
    }

    private void processEntry(JarEntry entry, InputStream entryIn) throws IOException {
        String name = entry.getName();
        String mappedName = this.remapper.map(name);

        // ensure the parent directory structure exists for the entry.
        processDirectory(mappedName, true);

        if (name.endsWith(".class")) {
            processClass(name, entryIn);
        } else if (name.equals("META-INF/MANIFEST.MF")) {
            processManifest(name, entryIn, entry.getTime());
        } else if (!this.resources.contains(mappedName)) {
            processResource(mappedName, entryIn, entry.getTime());
        }
    }

    private void processDirectory(String name, boolean parentsOnly) throws IOException {
        int index = name.lastIndexOf('/');
        if (index != -1) {
            String parentDirectory = name.substring(0, index);
            if (!this.resources.contains(parentDirectory)) {
                processDirectory(parentDirectory, false);
            }
        }

        if (parentsOnly) {
            return;
        }

        // directory entries must end in "/"
        JarEntry entry = new JarEntry(name + "/");
        this.jarOut.putNextEntry(entry);
        this.resources.add(name);
    }

    private void processManifest(String name, InputStream entryIn, long lastModified) throws IOException {
        Manifest in = new Manifest(entryIn);
        Manifest out = new Manifest();

        out.getMainAttributes().putAll(in.getMainAttributes());

        for (Map.Entry<String, Attributes> entry : in.getEntries().entrySet()) {
            Attributes outAttributes = new Attributes();
            for (Map.Entry<Object, Object> property : entry.getValue().entrySet()) {
                String key = property.getKey().toString();
                if (!SIGNATURE_PROPERTY_PATTERN.matcher(key).matches()) {
                    outAttributes.put(property.getKey(), property.getValue());
                }
            }
            out.getEntries().put(entry.getKey(), outAttributes);
        }

        JarEntry jarEntry = new JarEntry(name);
        jarEntry.setTime(lastModified);
        this.jarOut.putNextEntry(jarEntry);

        out.write(this.jarOut);

        this.resources.add(name);
    }

    private void processResource(String name, InputStream entryIn, long lastModified) throws IOException {
        JarEntry jarEntry = new JarEntry(name);
        jarEntry.setTime(lastModified);

        this.jarOut.putNextEntry(jarEntry);
        copy(entryIn, this.jarOut);

        this.resources.add(name);
    }

    private void processClass(String name, InputStream entryIn) throws IOException {
        ClassReader classReader = new ClassReader(entryIn);
        ClassWriter classWriter = new ClassWriter(0);
        RelocatingClassVisitor classVisitor = new RelocatingClassVisitor(classWriter, this.remapper, name);

        try {
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        } catch (Throwable e) {
            throw new RuntimeException("Error processing class " + name, e);
        }

        byte[] renamedClass = classWriter.toByteArray();

        // Need to take the .class off for remapping evaluation
        String mappedName = this.remapper.map(name.substring(0, name.indexOf('.')));

        // Now we put it back on so the class file is written out with the right extension.
        this.jarOut.putNextEntry(new JarEntry(mappedName + ".class"));
        this.jarOut.write(renamedClass);
    }
}
