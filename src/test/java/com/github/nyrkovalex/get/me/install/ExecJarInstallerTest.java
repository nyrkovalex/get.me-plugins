package com.github.nyrkovalex.get.me.install;

import com.github.nyrkovalex.get.me.api.GetMe;
import com.github.nyrkovalex.seed.Sys;
import com.github.nyrkovalex.seed.test.MockedTest;
import com.gtihub.nyrkovalex.seed.nio.Fs;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecJarInstallerTest extends MockedTest {

	@Mock Fs fs;
	@Mock Sys.Environment env;
	@Mock GetMe.ExecutionContext context;
	@Mock Path sourceJar;
	@Mock Path targetFile;
	@Mock Path jarFileName;
	@Mock Path jarPath;
	@Mock Path workingDir;
	@InjectMocks ExecJarInstaller installer;

	Optional<JarParams> params;

	@Before
	public void setUp() throws Exception {
		when(context.getCwd()).thenReturn(workingDir);
		when(fs.exists(sourceJar)).thenReturn(true);
		when(sourceJar.getFileName()).thenReturn(jarFileName);
		when(env.readVar("JARPATH")).thenReturn(Optional.of("jarPath"));
		when(fs.path("jarPath")).thenReturn(jarPath);
		when(workingDir.resolve("myJar")).thenReturn(sourceJar);
		when(jarPath.resolve(jarFileName)).thenReturn(targetFile);

		params = Optional.of(new JarParams("myJar"));
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowWhenNoJarParamFound() throws Exception {
		installer.exec(context, Optional.of(new JarParams()));
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowOnEmptyJarParam() throws Exception {
		installer.exec(context, Optional.of(new JarParams("")));
	}

	@Test
	public void testShouldCopyExecutableJarToJarPath() throws Exception {
		installer.exec(context, params);
		verify(fs).copy(sourceJar, targetFile, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowIfNoJarExists() throws Exception {
		when(fs.exists(sourceJar)).thenReturn(false);
		installer.exec(context, params);
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowIfJarPathIsNotSet() throws Exception {
		when(env.readVar("JARPATH")).thenReturn(Optional.<String>empty());
		installer.exec(context, params);
	}

}
