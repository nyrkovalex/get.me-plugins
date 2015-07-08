package com.github.nyrkovalex.get.me.mvn;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.nyrkovalex.get.me.api.GetMe;

@RunWith(Enclosed.class)
public class MvnBuilderTest {

	public static class BuilderTest {

		@Mock Mvn mvn;
		@Mock Mvn.Runner runner;
		@Mock Path workingDir;
		@InjectMocks MvnBuilder builder;

		@Before
		public void setUp() throws Exception {
			MockitoAnnotations.initMocks(this);
			when(mvn.run(anyObject())).thenReturn(runner);
		}

		@Test
		public void testShouldUseDefaultTargets() throws Exception {
			builder.exec(workingDir, Optional.of(new MvnParams()));
			verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
		}

		@Test
		public void testShouldUseProvidedTargets() throws Exception {
			List<String> targets = Arrays.asList("bar", "baz");
			builder.exec(workingDir, Optional.of(new MvnParams(targets)));
			verify(mvn).run(targets);
		}

		@Test
		public void testShouldUseDefaultTargetsOnNullParams() throws Exception {
			Optional<MvnParams> params = Optional.empty();
			builder.exec(workingDir, params);
			verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
		}

		@Test
		public void testShouldUseDefaultTargetsOnNullGoals() throws Exception {
			builder.exec(workingDir, Optional.of(new MvnParams()));
			verify(mvn).run(MvnBuilder.DEFAULT_GOALS);
		}
	}

	public static class RunnerTest {

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
			new Mvn.Runner(api, goals).in(workingDir);
		}

		@Test
		public void testShouldSetWokingDir() throws Exception {
			verify(invoker).setWorkingDirectory(workingDirFile);
		}

		@Test
		public void testShouldSetTargets() throws Exception {
			verify(req).setGoals(goals);
		}

		@Test
		public void testShouldSetPomFile() throws Exception {
			verify(req).setPomFile(pomFile);
		}

		@Test
		public void testShouldRunMaven() throws Exception {
			verify(invoker).execute(req);
		}

		@Test(expected = GetMe.Err.class)
		public void testShouldThrowOnMavenError() throws Exception {
			when(invoker.execute(req)).thenThrow(new MavenInvocationException("failed"));
			new Mvn.Runner(api, goals).in(workingDir);
		}
	}

}
