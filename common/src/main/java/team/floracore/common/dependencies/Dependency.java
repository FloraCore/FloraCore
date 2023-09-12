package team.floracore.common.dependencies;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import team.floracore.common.dependencies.relocation.Relocation;
import team.floracore.common.dependencies.relocation.RelocationHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

/**
 * The dependencies used by FloraCore.
 */
public enum Dependency {
	BYTE_BUDDY_AGENT("net.bytebuddy",
			"byte-buddy-agent",
			"1.14.4",
			"+9GrPbQ8bHi4gEkIy5W2VlF/XILn/ejSVdi9zu9BLXA="),
	ADVENTURE("net.kyori",
			"adventure-api",
			"4.14.0",
			"HUzIW6vEmRdGk2L9tLzSjvvIHIblK/Rz0Wful8DsXHY="),
	ADVENTURE_NBT("net.kyori",
			"adventure-nbt",
			"4.14.0",
			"FF8HplMgDkxHqv6rasPqwEjAYWKdgqNliTxP7rUwwk8="),
	ADVENTURE_KEY("net.kyori",
			"adventure-key",
			"4.14.0",
			"CyvXQ1OTJZXIV699Y3IQ+3gZ37H8W1KgTu+5OOqEeXI="),
	ADVENTURE_PLATFORM_API("net.kyori",
			"adventure-platform-api",
			"4.3.0",
			"7GBGKMK3wWXqdMH8s6LQ8DNZwsd6FJYOOgvC43lnCsI="),
	ADVENTURE_PLATFORM_FACET("net.kyori",
			"adventure-platform-facet",
			"4.3.0",
			"IPjm2zTXIqSszL7cybbALo7ms8q5NQsGqz0cCwuLRU8="),
	ADVENTURE_PLATFORM_BUKKIT("net.kyori",
			"adventure-platform-bukkit",
			"4.3.0",
			"Eh8BQf0ORHTty0Av3ru5hO1mycbnhSAISdhfx77uQNk="),
	ADVENTURE_PLATFORM_BUNGEECORD("net.kyori",
			"adventure-platform-bungeecord",
			"4.3.0",
			"C3hcnfV+7Y0Ol2HFFcM0+iX8GaBCOx+2PgQJfhmNOp8="),
	ADVENTURE_TEXT_SERIALIZER_BUNGEECORD("net.kyori",
			"adventure-text-serializer-bungeecord",
			"4.3.0",
			"4bw3bG3HohAAFgFXNc5MzFNNKya/WrgqrHUcUDIFbDk="),
	ADVENTURE_TEXT_SERIALIZER_LEGACY("net.kyori",
			"adventure-text-serializer-legacy",
			"4.14.0",
			"R6mPgWydO9n7V0anszM5hGIcC+RFBrDXqaIfq/6drY8="),
	ADVENTURE_TEXT_SERIALIZER_GSON("net.kyori",
			"adventure-text-serializer-gson",
			"4.14.0",
			"FbS8Ow7GxOvnIoifuL5YatPbHn+IxOjHLT/0PozszRg="),
	ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL("net.kyori",
			"adventure-text-serializer-gson-legacy-impl",
			"4.14.0",
			"BKhzODN0v4SSwvGT5gUh9ClVhyP86p4j/0VirAn4hws="),
	ADVENTURE_TEXT_SERIALIZER_JSON("net.kyori",
			"adventure-text-serializer-json",
			"4.14.0",
			"YtFclwyPM2iQ8R8PdJLHwBAWvNKxhjVkNcFVy99gnMs="),
	ADVENTURE_TEXT_SERIALIZER_JSON_LEGACY_IMPL("net.kyori",
			"adventure-text-serializer-json-legacy-impl",
			"4.14.0",
			"qvHaGmoL6Qoo2tro2bFiWi77bhcVbYKhISKnXQ0GQMs="),
	ADVENTURE_TEXT_SERIALIZER_PLAIN("net.kyori",
			"adventure-text-serializer-plain",
			"4.14.0",
			"2x0RS1D3bBsdQz3OQuUhemvzWs3ChCFhr7jKnP2eyQA="),
	EXAMINATION_API("net.kyori", "examination-api",
			"1.3.0",
			"ySN//ssFQo9u/4YhYkascM4LR7BMCOp8o1Ag/eV/hJI="),
	CLOUD_CORE("cloud{}commandframework", "cloud-core",
			"1.8.3",
			"FhrRDVk/aT0nFyWOicF30WIXc0FEEdgkU6UqL/dXsEg=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_ANNOTATIONS("cloud{}commandframework",
			"cloud-annotations",
			"1.8.3",
			"oy6LaeFidBbRW6GgZ5Kmo3900bKsfaqGJlFEMcjXQ8w=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_PAPER("cloud{}commandframework",
			"cloud-paper",
			"1.8.3",
			"iVYGvszDPm0Zlytga0teNQJ7Ekojn72HI6/RqK3boYU=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_BUKKIT("cloud{}commandframework",
			"cloud-bukkit",
			"1.8.3",
			"FV96thssXMaB8bOzkNb7GyqQYyyvcFuJ2g8xafclTa8=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_BUNGEE("cloud{}commandframework",
			"cloud-bungee",
			"1.8.3",
			"xN0+H+e0HV+KRugc7jpd+YZ5Wz8i+cOOWW0YqJ4ATRE=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_BRIGADIER("cloud{}commandframework",
			"cloud-brigadier",
			"1.8.3",
			"/T19TrFV+afTyk9fS4ke584XK149ImNuXfvRmRGUgrU=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_SERVICES("cloud{}commandframework",
			"cloud-services", "1.8.3",
			"fjceKOp9boNjZmD4bRwVfh2nvrEFtcQcMj8xd+FbSk0=",
			Relocation.of("cloud", "cloud{}commandframework")),
	CLOUD_TASKS("cloud{}commandframework",
			"cloud-tasks", "1.8.3",
			"0wnkKoCA/ZtiwvWgrS6PheJZfvVhP0LObX3duUMEJlg=",
			Relocation.of("cloud", "cloud{}commandframework")),
	MARIADB_DRIVER("org{}mariadb{}jdbc",
			"mariadb-java-client",
			"3.1.3",
			"ESl+5lYkJsScgTh8hgFTy8ExxMPQQkktT20tl6s6HKU=",
			Relocation.of("mariadb", "org{}mariadb{}jdbc")),
	MYSQL_DRIVER("mysql",
			"mysql-connector-java",
			"8.0.30",
			"tb8vCYcZfDCt90qeQZuJzaTCV9otEUKHH1CEFtXyIno=",
			Relocation.of("mysql", "com{}mysql")),
	POSTGRESQL_DRIVER("org{}postgresql",
			"postgresql",
			"42.6.0",
			"uBfGekDJQkn9WdTmhuMyftDT0/rkJrINoPHnVlLPxGE=",
			Relocation.of("postgresql", "org{}postgresql")),
	/**
	 * seems to be a compat bug in 1.4.200 with older dbs
	 * {@see <a href="https://github.com/h2database/h2database/issues/2078">H2</a>}
	 * <p>
	 * we don't apply relocations to h2 - it gets loaded via an isolated classloader
	 */
	H2_DRIVER_LEGACY("com.h2database",
			"h2",
			"1.4.199",
			"MSWhZ0O8a0z7thq7p4MgPx+2gjCqD9yXiY95b5ml1C4="),
	/**
	 * we don't apply relocations to h2 - it gets loaded via an isolated classloader
	 */
	H2_DRIVER("com.h2database",
			"h2",
			"2.1.214",
			"1iPNwPYdIYz1SajQnxw5H/kQlhFrIuJHVHX85PvnK9A="),
	/**
	 * we don't apply relocations to sqlite - it gets loaded via an isolated classloader
	 */
	SQLITE_DRIVER("org.xerial",
			"sqlite-jdbc",
			"3.41.2.1",
			"sxQQV7+2heqfmrIAwhWLwotHxKLULIj8sKGgCVT60m0="),
	HIKARI("com{}zaxxer",
			"HikariCP",
			"4.0.3",
			"fAJK7/HBBjV210RTUT+d5kR9jmJNF/jifzCi6XaIxsk=",
			Relocation.of("hikari", "com{}zaxxer{}hikari")),
	SLF4J_SIMPLE("org.slf4j",
			"slf4j-simple",
			"2.0.7",
			"UOrj8cyaeKlwlwUY4AXT9D1c0yYtI09H6988o/i8Aac="),
	SLF4J_API("org.slf4j",
			"slf4j-api",
			"2.0.7",
			"XWKYuToZBcMs2mR4gIrBTC1KR+kVNeU8Qff+64XZRvQ="),
	GEANTYREF("io.leangen.geantyref",
			"geantyref",
			"1.3.14",
			"Yrci0TJFRQOQSl97CkeiTw/FgYIeyNNoffHg8UbG1h4="),
	OKHTTP("com{}squareup{}" + RelocationHelper.OKHTTP3_STRING,
			"okhttp",
			"3.14.9",
			"JXD6tVUVy/iB16TO70n8UVSQvAJwV+Zmd2ooMkZa7KA=",
			Relocation.of(RelocationHelper.OKHTTP3_STRING, RelocationHelper.OKHTTP3_STRING),
			Relocation.of(RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_STRING)),
	OKIO("com{}squareup{}" + RelocationHelper.OKIO_STRING,
			RelocationHelper.OKIO_STRING,
			"1.17.5",
			"Gaf/SNhtPPRJf38lD78pX0MME6Uo3Vt7ID+CGAK4hq0=",
			Relocation.of(RelocationHelper.OKIO_STRING, RelocationHelper.OKIO_STRING)),
	CAFFEINE("com.github.ben-manes.caffeine",
			"caffeine", "2.8.2", "U60wqcyKOJZbqkmGQoDP5zyINxyvZoDFGd5CqqNvd64="),
	UNSAFE_ACCESSOR("io.github.karlatemp",
			"unsafe-accessor",
			"1.7.0",
			"NEbDztyvV/6NlpxBR1TtzOm6qy5OxBL/UsnXBSopp2g="),
	OPENCSV("com.opencsv",
			"opencsv",
			"5.7.1",
			"0Fp70l/WK/J4A9cbgPmK0tkpQgByZIwJom1FNE0l1rg="),
	JEDIS("redis.clients",
			"jedis",
			"4.3.1",
			"WXiUJE5C4bMXFHDpKUeBgk2/YXlJ53qgIw6qPsR3LbQ=",
			Relocation.of("jedis", "redis{}clients{}jedis"),
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")),
	COMMONS_POOL_2("org.apache.commons",
			"commons-pool2",
			"2.11.1",
			"6gUF7nUV5YsawOaG5NGl2ffYCOJRphvDcaoFlbmWP4M=",
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")),
	BUKKIT_GUI("com.huanmeng-qwq",
			"Bukkit-Gui",
			"2.1.2",
			"Q5Ps+7W+UaUsbFjZYRUl44F27z4vfhhixfhhpkBJLoM="),
	BSTATS_BUKKIT("org{}bstats",
			"bstats-bukkit",
			"3.0.2",
			"cnhDWq1aF0MzfmH1FU5xDRxAVQndKoke1vYC3ugeslA=",
			Relocation.of("bstats", "org{}bstats")),
	BSTATS_BUNGEE("org{}bstats",
			"bstats-bungeecord",
			"3.0.2",
			"5qXlyXgK2Njk+OHPp/ju/ndAdZxuKytHREV/rSYsZIo=",
			Relocation.of("bstats", "org{}bstats")),
	BSTATS_BASE("org{}bstats",
			"bstats-base",
			"3.0.2",
			"r4oL2YjL+ZvIXULaTASrt9X4s9+f/JC5dup+dhmEZrk=",
			Relocation.of("bstats", "org{}bstats")),
	COMMONS_IO("commons-io",
			"commons-io",
			"2.13.0",
			"Zx6qOWiNrC/6pGRbPJmAri0OokceSual2hmc0VriNmY="),
	PAPER_LIB("io.papermc",
			"paperlib",
			"1.0.7",
			"QlP2zk4m71YeB/QEcdnOGkuz6iL0+cDDzTeDMxFB1+4=",
			Relocation.of("paperlib", "io{}papermc{}lib")),
	CROWDIN("com.github.crowdin",
			"crowdin-api-client-java",
			"1.10.0",
			"CbdIEz3n8Ka0zKAdRka1GHVtDDxrhLc7Bt4TWgZhFww="),
	HTTP_CORE("org.apache.httpcomponents",
			"httpcore",
			"4.4.16",
			"bJs90UKgncRo4jrTmq1vdaDyuFElEERp8CblKkdORk8="),
	HTTP_CLIENT("org.apache.httpcomponents",
			"httpclient",
			"4.5.14",
			"yLx+HFGm1M5y9A0uu6vxxLaL/nbnMhBLBDgbSTR46dY="),
	JACKSON_DATABIND("com.fasterxml.jackson.core",
			"jackson-databind",
			"2.15.2",
			"DrL9rW5Aq4gyp4ybIvWBlt2XBZTo09Wibq2HhHxPOpY="),
	JACKSON_CORE("com.fasterxml.jackson.core",
			"jackson-core",
			"2.15.2",
			"MDyZ6CsfqpGguuXY++tW9+Kt+bUmqQDdcjvxQNYr1LQ="),
	JACKSON_ANNOTATIONS("com.fasterxml.jackson.core",
			"jackson-annotations",
			"2.15.2",
			"BOIflNz+5LB4+lpfUwR7eFqrpp0Z3jkvYW56f+XTiC8="),
	COMMONS_LOGGING("commons-logging",
			"commons-logging",
			"1.2",
			"2t3qHqC+D1aXirMAa4rJKDSv7vvZt+TmMW/KV98PpjY="),
	RHINO("org.mozilla",
			"rhino",
			"1.7.14",
			"ySkLDYAb8Nu7xEM44Pdpt2UKDF0E5rsa64V3XAIRsAM="),
	MONGODB_DRIVER_CORE(
			"org.mongodb",
			"mongodb-driver-core",
			"4.5.0",
			"awqoW0ImUcrCTA2d1rDCjDLEjLMCrOjKWIcC7E+zLGA=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_LEGACY(
			"org.mongodb",
			"mongodb-driver-legacy",
			"4.5.0",
			"77KZGIr3KZmzBpN69rGOLXmnlJIBCXRl/U4gEIdlFhY=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_SYNC(
			"org.mongodb",
			"mongodb-driver-sync",
			"4.5.0",
			"q9XDSGJjlo/Ek6jHoCbqWnaK/dghB8y9aDM0hCLiSvk=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	MONGODB_DRIVER_BSON(
			"org.mongodb",
			"bson",
			"4.5.0",
			"6CFyEzxbdeiBEXdDBmcgqWs5dvicgFkBLU3MlQUIqRA=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	GEOIP_2("com{}maxmind{}geoip2",
			"geoip2",
			"2.16.1",
			"4A64QuI9yvK1zagrDgUd54nUOZyWCRzwO3Xf5Qw4W3w=",
			Relocation.of("maxmind", "com{}maxmind")),
	JAVATAR("javatar",
			"javatar",
			"2.5",
			"6bfUsc4okcRGOtL8bWUyASmYaAyA5BH7l1SV6KZpAe4=",
			Relocation.of("javatar", "com{}ice{}tar"));

	private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";
	private final String mavenRepoPath;
	private final String version;
	@Getter
	private final byte[] checksum;
	@Getter
	private final List<Relocation> relocations;

	Dependency(String groupId, String artifactId, String version, String checksum) {
		this(groupId, artifactId, version, checksum, new Relocation[0]);
	}

	Dependency(String groupId, String artifactId, String version, String checksum, Relocation... relocations) {
		this.mavenRepoPath = String.format(MAVEN_FORMAT,
				rewriteEscaping(groupId).replace(".", "/"),
				rewriteEscaping(artifactId),
				version,
				rewriteEscaping(artifactId),
				version);
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

	public boolean checksumMatches(byte[] hash) {
		return Arrays.equals(this.checksum, hash);
	}

}
