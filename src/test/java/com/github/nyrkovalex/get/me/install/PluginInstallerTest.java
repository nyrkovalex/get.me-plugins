package com.github.nyrkovalex.get.me.install;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.gtihub.nyrkovalex.seed.nio.Fs;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PluginInstallerTest {

	@Mock private Path jarFile;
	@Mock private Fs fs;
	@Mock private Path pluginsDir;
	@Mock private Path workingDir;
	@Mock private GetMe.Environment environment;
	@Mock private GetMe.ExecutionContext context;

	@InjectMocks private PluginInstaller installer;

	private final JarParams params = new JarParams("plugin.jar");

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(context.getCwd()).thenReturn(workingDir);
		when(workingDir.resolve("plugin.jar")).thenReturn(jarFile);
		when(environment.pluginsHome()).thenReturn("/home/me/get.me/plugins");
		when(fs.path("/home/me/get.me/plugins")).thenReturn(pluginsDir);
	}


	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowWhenNoJarParamsPresent() throws Exception {
		installer.exec(context, Optional.empty());
	}

	@Test
	public void testShouldCopyJarToPluginsDir() throws Exception {
		installer.exec(context, Optional.of(params));
		verify(fs).copy(jarFile, pluginsDir, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldRethrowIoError() throws Exception {
		when(fs.copy(jarFile, pluginsDir, StandardCopyOption.REPLACE_EXISTING)).thenThrow(new IOException());
		installer.exec(context, Optional.of(params));
	}
}
