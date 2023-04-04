package team.floracore.common.dependencies;

import com.google.common.collect.*;
import team.floracore.common.dependencies.relocation.*;

import java.security.*;
import java.util.*;

/**
 * The dependencies used by LuckPerms.
 */
public enum Dependency {
    ASM("org.ow2.asm", "asm", "9.1", "zaTeRV+rSP8Ly3xItGOUR9TehZp6/DCglKmG8JNr66I="),
    ASM_COMMONS("org.ow2.asm", "asm-commons", "9.1", "r8sm3B/BLAxKma2mcJCN2C4Y38SIyvXuklRplrRwwAw="),
    JAR_RELOCATOR("me.lucko", "jar-relocator", "1.4", "1RsiF3BiVztjlfTA+svDCuoDSGFuSpTZYHvUK8yBx8I="),
    MARIADB_DRIVER("org{}mariadb{}jdbc", "mariadb-java-client", "3.1.3", "ESl+5lYkJsScgTh8hgFTy8ExxMPQQkktT20tl6s6HKU=", Relocation.of("mariadb", "org{}mariadb{}jdbc")),
    MYSQL_DRIVER("mysql", "mysql-connector-java", "8.0.30", "tb8vCYcZfDCt90qeQZuJzaTCV9otEUKHH1CEFtXyIno=", Relocation.of("mysql", "com{}mysql")),
    POSTGRESQL_DRIVER("org{}postgresql", "postgresql", "42.6.0", "uBfGekDJQkn9WdTmhuMyftDT0/rkJrINoPHnVlLPxGE=", Relocation.of("postgresql", "org{}postgresql")),
    /**
     * we don't apply relocations to h2 - it gets loaded via an isolated classloader
     */
    H2_DRIVER("com.h2database", "h2", "2.1.214", "1iPNwPYdIYz1SajQnxw5H/kQlhFrIuJHVHX85PvnK9A="),
    /**
     * we don't apply relocations to sqlite - it gets loaded via an isolated classloader
     */
    SQLITE_DRIVER("org.xerial", "sqlite-jdbc", "3.41.2.1", "sxQQV7+2heqfmrIAwhWLwotHxKLULIj8sKGgCVT60m0="),
    HIKARI("com{}zaxxer", "HikariCP", "4.0.3", "fAJK7/HBBjV210RTUT+d5kR9jmJNF/jifzCi6XaIxsk=", Relocation.of("hikari", "com{}zaxxer{}hikari")),
    SLF4J_SIMPLE("org.slf4j", "slf4j-simple", "2.0.7", "UOrj8cyaeKlwlwUY4AXT9D1c0yYtI09H6988o/i8Aac="),
    SLF4J_API("org.slf4j", "slf4j-api", "2.0.7", "XWKYuToZBcMs2mR4gIrBTC1KR+kVNeU8Qff+64XZRvQ="),
    ;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";
    private final String mavenRepoPath;
    private final String version;
    private final byte[] checksum;
    private final List<Relocation> relocations;

    Dependency(String groupId, String artifactId, String version, String checksum) {
        this(groupId, artifactId, version, checksum, new Relocation[0]);
    }

    Dependency(String groupId, String artifactId, String version, String checksum, Relocation... relocations) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT, rewriteEscaping(groupId).replace(".", "/"), rewriteEscaping(artifactId), version, rewriteEscaping(artifactId), version);
        this.version = version;
        this.checksum = Base64.getDecoder().decode(checksum);
        this.relocations = ImmutableList.copyOf(relocations);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    /**
     * Creates a {@link java.security.MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileName(String classifier) {
        String name = name().toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty() ? "" : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

}
