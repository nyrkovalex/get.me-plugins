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
		try {
			Path pluginsDir = fs.path(environment.pluginsHome());
			Path targetJar = context.getCwd().resolve(jarParams.jar);
			fs.copy(targetJar, pluginsDir, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException err) {
			throw new GetMe.PluginException(
					String.format("Failed to copy %s to %s", jarParams.jar, environment.pluginsHome()),
					err
			);
		}
	}

	@Override
	public Optional<Class<JarParams>> paramsClass() {
		return Optional.of(JarParams.class);
	}
}
