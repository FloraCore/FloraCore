package team.floracore.common.dependencies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyChecksumTest {
    @ParameterizedTest
    @EnumSource
    public void checksumMatches(Dependency dependency) {
        byte[] hash = getHash(dependency);
        System.out.printf("Checking for dependency %s...%n", dependency.name());
        assertTrue(dependency.checksumMatches(hash),
                "Dependency " + dependency.name() + " has hash " + Base64.getEncoder().encodeToString(hash));
    }

    /**
     * 之所以注释了,是因为编译的时候太慢了。
     */
    @Test
    public void getChecksumMatches() {
        for (Dependency dependency : Dependency.values()) {
            try {
                getChecksumMatches(dependency);
            } catch (DependencyDownloadException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ParameterizedTest
    @EnumSource
    public void getChecksumMatches(Dependency dependency) throws DependencyDownloadException {
        byte[] hash = getHash(dependency);
        System.out.printf("Checking for dependency %s...%n", dependency.name());
        if (dependency.checksumMatches(hash)) {
            System.out.printf("Checking for dependency %s is OK!%n", dependency.name());
        } else {
            System.out.printf("Dependency %s new hash is %s%n",
                    dependency.name(),
                    Base64.getEncoder().encodeToString(hash));
        }
        assertTrue(dependency.checksumMatches(hash));
    }

    public byte[] getHash(Dependency dependency) {
        for (DependencyRepository value : DependencyRepository.values()) {
            try {
                return Dependency.createDigest()
                        .digest(value.downloadRaw(dependency));
            } catch (DependencyDownloadException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException();
    }

}
