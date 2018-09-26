package eu.europa.eusurvey.katalon.controllers;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import org.junit.Assert;


public class SingleControllerTest {
  
  // Tests for containsBrowser
  @Test
  public void containsBrowserAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertTrue(Whitebox.invokeMethod(singleController, "containsBrowser", new Object[] { "Chrome",new File("src/test/resources")}));
  }
  
  @Test(expected = NullPointerException.class)
  public void containsBrowserNONAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertTrue(Whitebox.invokeMethod(singleController, "containsBrowser", new Object[] { null , new File("")}));
  }
  
  @Test(expected = NullPointerException.class)
  public void containsBrowserNONAcceptableValues2Test() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertTrue(Whitebox.invokeMethod(singleController, "containsBrowser", new Object[] {"", null}));
  }
  
  // Tests for getLatestReportForTestSuiteFile
  @Test
  public void getLatestReportForTestSuiteFileAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertNotNull(Whitebox.invokeMethod(singleController, "getLatestReportForTestSuiteFile", new Object[] {new File("src/test/resources/sample_directory"),"Chrome"}));
  }
  
  @Test(expected = IOException.class)
  public void getLatestReportForTestSuiteFileNONAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertNotNull(Whitebox.invokeMethod(singleController, "getLatestReportForTestSuiteFile", new Object[] {new File("src/test/resources/sample_directory_error"),"Chrome"}));
  }
  
  
  // Tests for getExecutedTestSuiteDirectory
  @Test
  public void getExecutedTestSuiteDirectoryAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertNotNull(Whitebox.invokeMethod(singleController, "getExecutedTestSuiteDirectory", new Object[] {"Create Survey and Form Runner",new File("src/test/resources/sample_report_directory")}));
  }
  
  @Test(expected = IOException.class)
  public void getExecutedTestSuiteDirectoryNONAcceptableValuesTest() throws Exception {
    SingleController singleController = new SingleController();
    Assert.assertNotNull(Whitebox.invokeMethod(singleController, "getExecutedTestSuiteDirectory", new Object[] {"Create Survey and Form Runner",new File("src/test/resources/sample_report_directory_error")}));
  }
  
  
  

}
