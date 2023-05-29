package team.floracore.common.dependencies;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DependencyChecksumTest {
    @ParameterizedTest
    @EnumSource
    public void checksumMatches(Dependency dependency) throws DependencyDownloadException {
        byte[] hash = Dependency.createDigest()
                .digest(DependencyRepository.MAVEN_CENTRAL_MIRROR.downloadRaw(dependency));
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
        byte[] hash = Dependency.createDigest()
                .digest(DependencyRepository.MAVEN_CENTRAL_MIRROR.downloadRaw(dependency));
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

}
