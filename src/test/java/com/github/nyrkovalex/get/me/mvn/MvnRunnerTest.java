package com.github.nyrkovalex.get.me.mvn;

import com.github.nyrkovalex.get.me.api.GetMe;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MvnRunnerTest {

	@Mock MvnApi api;
	@Mock Invoker invoker;
	@Mock InvocationRequest req;
	@Mock InvocationResult res;
	@Mock Path workingDir;
	@Mock File workingDirFile;
	@Mock Path pom;
	@Mock File pomFile;

	List<String> goals = Arrays.asList("foo", "bar");

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(api.invocationRequest()).thenReturn(req);
		when(api.invoker()).thenReturn(invoker);
		when(invoker.execute(anyObject())).thenReturn(res);
		when(workingDir.resolve(Mvn.POM_XML_NAME)).thenReturn(pom);
		when(pom.toFile()).thenReturn(pomFile);
		when(workingDir.toFile()).thenReturn(workingDirFile);
	}

	@Test
	public void testShouldSetWokingDir() throws Exception {
		new Mvn.Runner(api, goals).in(workingDir);
		verify(invoker).setWorkingDirectory(workingDirFile);
	}

	@Test
	public void testShouldSetTargets() throws Exception {
		new Mvn.Runner(api, goals).in(workingDir);
		verify(req).setGoals(goals);
	}

	@Test
	public void testShouldSetPomFile() throws Exception {
		new Mvn.Runner(api, goals).in(workingDir);
		verify(req).setPomFile(pomFile);
	}

	@Test
	public void testShouldRunMaven() throws Exception {
		new Mvn.Runner(api, goals).in(workingDir);
		verify(invoker).execute(req);
	}

	@Test
	public void testShouldSuppressOutputByDefault() throws Exception {
		new Mvn.Runner(api, goals).in(workingDir);
		verify(invoker).setOutputHandler(null);
	}

	@Test
	public void testShouldEnableOuputMode() throws Exception {
		new Mvn.Runner(api, goals).enableOutput(true).in(workingDir);
		verify(invoker, never()).setOutputHandler(null);
	}

	@Test(expected = GetMe.PluginException.class)
	public void testShouldThrowOnMavenError() throws Exception {
		when(invoker.execute(req)).thenThrow(new MavenInvocationException("failed"));
		new Mvn.Runner(api, goals).in(workingDir);
	}
}
