package com.github.nyrkovalex.get.me.install;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.gtihub.nyrkovalex.seed.nio.Fs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class PluginInstaller implements GetMe.Plugin<JarParams> {

	private final GetMe.Environment environment;
	private final Fs fs;

	public PluginInstaller() {
		this(GetMe.environment(), Fs.instance());
	}

	PluginInstaller(GetMe.Environment environment, Fs fs) {
		this.environment = environment;
		this.fs = fs;
	}

	@Override
	public void exec(GetMe.ExecutionContext context, Optional<JarParams> params) throws GetMe.PluginException {
		JarParams jarParams = params.orElseThrow(
				() -> new GetMe.PluginException("`jar` parameter must be provided"));
		Path sourceJarPath = context.getCwd().resolve(jarParams.jar);
		Path targetJarPath = fs.path(environment.pluginsHome()).resolve(sourceJarPath.getFileName());
		try {
			fs.copy(sourceJarPath, targetJarPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new GetMe.PluginException(
					String.format("Failed to copy %s to %s", sourceJarPath, targetJarPath),
					ex
			);
		}
	}

	@Override
	public Optional<Class<JarParams>> paramsClass() {
		return Optional.of(JarParams.class);
	}
}
