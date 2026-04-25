package com.blissfuljuan.aiprojecteval;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ArchitectureScaffoldTests {

	private static final Path PACKAGE_ROOT =
			Path.of("src/main/java/com/blissfuljuan/aiprojecteval");

	private static final Set<String> MAJOR_MODULES = Set.of(
			"identity",
			"project",
			"submission",
			"document",
			"evaluation",
			"ai",
			"repositoryanalysis",
			"deploymentvalidation",
			"report"
	);

	private static final Set<String> MODULE_PACKAGES = Set.of(
			"controller",
			"service",
			"repository",
			"model",
			"dto",
			"mapper"
	);

	@Test
	void majorModulesUseLocalFeaturePackages() {
		MAJOR_MODULES.forEach(module -> {
			assertThat(PACKAGE_ROOT.resolve(module))
					.as("feature module package: %s", module)
					.isDirectory();

			MODULE_PACKAGES.forEach(slice -> assertThat(PACKAGE_ROOT.resolve(module).resolve(slice))
					.as("%s.%s package", module, slice)
					.isDirectory());
		});
	}

	@Test
	void commonModuleOwnsSharedConcerns() {
		assertThat(PACKAGE_ROOT.resolve("common")).isDirectory();
		assertThat(PACKAGE_ROOT.resolve("common").resolve("response")).isDirectory();
		assertThat(PACKAGE_ROOT.resolve("common").resolve("exception")).isDirectory();
		assertThat(PACKAGE_ROOT.resolve("common").resolve("security")).isDirectory();
		assertThat(PACKAGE_ROOT.resolve("common").resolve("util")).isDirectory();
		assertThat(PACKAGE_ROOT.resolve("common").resolve("controller")).isDirectory();
	}

	@Test
	void rootPackageDoesNotUseGlobalLayerPackagesOutsideModules() throws IOException {
		Set<String> rootDirectories;
		try (Stream<Path> paths = Files.list(PACKAGE_ROOT)) {
			rootDirectories = paths
					.filter(Files::isDirectory)
					.map(path -> path.getFileName().toString())
					.collect(Collectors.toSet());
		}

		assertThat(rootDirectories)
				.doesNotContain("controller", "controllers", "service", "services", "repository",
						"repositories", "model", "models", "dto", "dtos");
	}

	@Test
	void modulesDoNotImportRepositoriesFromOtherModules() throws IOException {
		for (String module : MAJOR_MODULES) {
			Path modulePath = PACKAGE_ROOT.resolve(module);
			try (Stream<Path> paths = Files.walk(modulePath)) {
				for (Path javaFile : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
					String source = Files.readString(javaFile);
					Set<String> forbiddenRepositoryImports = MAJOR_MODULES.stream()
							.filter(otherModule -> !otherModule.equals(module))
							.map(otherModule -> "com.blissfuljuan.aiprojecteval." + otherModule + ".repository")
							.collect(Collectors.toSet());

					assertThat(forbiddenRepositoryImports.stream().filter(source::contains).toList())
							.as("%s must not directly import another module's repository", javaFile)
							.isEmpty();
				}
			}
		}
	}

	@Test
	void aiModuleHasPluggableProviderBoundary() {
		assertThat(PACKAGE_ROOT.resolve("ai").resolve("provider").resolve("AIProvider.java")).isRegularFile();
		assertThat(PACKAGE_ROOT.resolve("ai").resolve("provider").resolve("OpenAIProvider.java")).isRegularFile();
		assertThat(PACKAGE_ROOT.resolve("ai").resolve("provider").resolve("OllamaProvider.java")).isRegularFile();
	}
}
