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
        for (DependencyRepository repo : DependencyRepository.values()) {
            byte[] hash = Dependency.createDigest().digest(repo.downloadRaw(dependency));
            System.out.printf("Checking for dependency %s...%n", dependency.name());
            assertTrue(dependency.checksumMatches(hash), "Dependency " + dependency.name() + " has hash " + Base64.getEncoder().encodeToString(hash));
        }
    }

    @ParameterizedTest
    @EnumSource
    public void getChecksumMatches(Dependency dependency) throws DependencyDownloadException {
        for (DependencyRepository repo : DependencyRepository.values()) {
            byte[] hash = Dependency.createDigest().digest(repo.downloadRaw(dependency));
            System.out.printf("Checking for dependency %s...%n", dependency.name());
            if (dependency.checksumMatches(hash)) {
                System.out.printf("Checking for dependency %s is OK!%n", dependency.name());
            } else {
                System.out.printf("Dependency %s new hash is %s%n", dependency.name(), Base64.getEncoder().encodeToString(hash));
            }
            assertTrue(dependency.checksumMatches(hash));
        }
    }

    @Test
    public void checksumMatches() {
        for (Dependency dependency : Dependency.values()) {
            try {
                checksumMatches(dependency);
            } catch (DependencyDownloadException e) {
                throw new RuntimeException(e);
            }
        }
    }

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

}
