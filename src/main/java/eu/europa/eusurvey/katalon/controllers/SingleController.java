package eu.europa.eusurvey.katalon.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.base.Preconditions;

import eu.europa.eusurvey.katalon.utils.DateTimeParser;

@RestController
public class SingleController {
  private static Logger logger = LoggerFactory.getLogger(SingleController.class);
  private static List<ProcessHandle> processList = new LinkedList<>();

  @Value("${git.executable.location}")
  private String gitExecutableLocation;

  @Value("${katalon.project.root.directory.location}")
  private String katalonProjectRootDirectoryLocation;

  @Value("${katalon.project.name.location}")
  private String katalonProjectNameLocation;

  @Value("${katalon.executable.location}")
  private String katalonExecutableLocation;

  @Value("${katalon.reports.root.directory.location}")
  private String katalonReportsRootDirectoryLocation;

  private static String status = "Nothing ran yet";

  @RequestMapping(value = "/start/{testSuite}/{browser}", method = RequestMethod.GET)
  public String answer(@PathVariable String testSuite, @PathVariable String browser) {
    logger.info("Received a demand to START the katalon tests {} on {}", testSuite, browser);

    String command = "\"" + katalonExecutableLocation + "\"" + " -runMode=console -projectPath="
        + "\"" + katalonProjectNameLocation + "\"" + " -testSuitePath=" + "\"" + "Test Suites/"
        + testSuite + "\"" + " -browserType=" + "\"" + browser + "\""
        + " -retry=0 -executionProfile=default -consoleLog";

    Process runningProcess = null;
    try {
      File newFile = new File("/temp/script.bat");
      FileUtils.writeByteArrayToFile(newFile, command.getBytes());
      runningProcess = (new ProcessBuilder().command(newFile.getAbsolutePath()).start());
      processList.add(runningProcess.toHandle());
      logger.info("Ran {}", command);
      logger.info("Process PID {} ", runningProcess.pid());
      return waitForRunningProcessWhileAppendingInputStream(runningProcess);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return e.getMessage();
    }
  }

  private String waitForRunningProcessWhileAppendingInputStream(Process runningProcess)
      throws InterruptedException, IOException {
    BufferedReader stdInput = new BufferedReader(
        new InputStreamReader(runningProcess.getInputStream()));

    BufferedReader stdError = new BufferedReader(
        new InputStreamReader(runningProcess.getErrorStream()));

    runningProcess.waitFor(30, TimeUnit.MINUTES);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<html>");

    String s = "";
    while ((s = stdInput.readLine()) != null) {
      logger.info(s);
      stringBuilder.append("<p>" + s + "</p>");
    }

    while ((s = stdError.readLine()) != null) {
      logger.error(s);
      stringBuilder.append(s);
    }

    stringBuilder.append("</html>");
    return stringBuilder.toString();
  }

  @RequestMapping(value = "/stop", method = RequestMethod.GET)
  public HttpStatus stop() {
    Predicate<Boolean> istrue = t -> t ;
    if (processList.stream().map(x->destroyFamily(x)).allMatch(istrue)) {
      return HttpStatus.OK;
    } else {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  @RequestMapping(value = "/latestReport/{testSuite}/{browser}", method = RequestMethod.GET)
  public ResponseEntity<Resource> latestReport(@PathVariable String testSuite,
      @PathVariable String browser) throws IOException {
    logger.info("Received a demand for the LATEST REPORT for the katalon tests {} on {}", testSuite,
        browser);

    File directory = getExecutedTestSuiteDirectory(testSuite,
        new File(katalonReportsRootDirectoryLocation));
    File junitReport = getLatestReportForTestSuiteFile(directory, browser);

    Path path = Paths.get(junitReport.getAbsolutePath());
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

    return ResponseEntity.ok().contentLength(junitReport.length())
        .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
  }
  
  private boolean destroy(ProcessHandle ph) {
    boolean destroyed = ph.destroyForcibly() || !ph.isAlive();
    logger.info("destroyed PID {} {}", ph.pid(), destroyed);
    logger.info("PID {} : command {}", ph.pid(), ph.info().command().orElse("[no value]"));
    return destroyed;
  }

  private boolean destroyFamily(ProcessHandle ph) {
    ph.descendants().forEach(x -> {
      destroy(x);
    });
    return destroy(ph);
  }

  @RequestMapping(value = "/status", method = RequestMethod.GET)
  public String status() {
    return status;
  }

  /**
   * Returns the test suite directory named <code>testSuiteName</code> in
   * <code>reportDirectory</code>
   * 
   * @throws IOException
   *           if <code>reportsLocation</code> is not a directory or is empty
   */
  private File getExecutedTestSuiteDirectory(String testSuiteName, File reportDirectory)
      throws IOException {
    Preconditions.checkNotNull(testSuiteName, "testSuiteName is null");
    if (!reportDirectory.isDirectory() || reportDirectory.listFiles().length == 0)
      throw new IOException("reportDirectory is not a directory or is empty");

    for (File fil : reportDirectory.listFiles()) {
      if (fil.getName().equals(testSuiteName)) {
        return fil;
      }
    }
    throw new IOException(
        "File " + katalonReportsRootDirectoryLocation + testSuiteName + " not found");
  }

  /**
   * Returns the latest JUnit report in <code>testSuiteReportDirectory</code>'s latest
   * sub-directory, for the browser <code>browser</code>.
   * 
   * @throws IOException
   *           if <code>testSuiteReportDirectory</code> is not a directory or is empty if a
   *           JunitReport could not be found in one subdirectory of
   *           <code>testSuiteReportDirectory</code> named YYYYMMdd_HHmmss
   */
  private File getLatestReportForTestSuiteFile(File testSuiteReportDirectory, String browser)
      throws IOException {
    Preconditions.checkNotNull(browser, "browser is null");
    Preconditions.checkNotNull(testSuiteReportDirectory, "testSuiteReportDirectory is null");

    if (!testSuiteReportDirectory.isDirectory() || testSuiteReportDirectory.listFiles().length == 0)
      throw new IOException("The test suite report directory " + testSuiteReportDirectory.getPath()
          + " is actually not a directory or is empty");
    File latestReportDirectory = new File("/temp/19700101_000000");
    boolean found = false;
    for (File f : testSuiteReportDirectory.listFiles()) {
      if ((DateTimeParser.parseDateTime(f.getName()) != null)
          && DateTimeParser.parseDateTime(f.getName())
              .after(DateTimeParser.parseDateTime(latestReportDirectory.getName()))) {
        try {
          if (containsBrowser(browser, f)) {
            latestReportDirectory = f;
            found = true;
          }
        } catch (ParseException e) {
          throw new IOException(e);
        }
      }
    }
    if (found)
      return getJunitReportInDirectory(latestReportDirectory);
    else
      throw new IOException(
          "Could not find JUnit report in  " + latestReportDirectory.getAbsolutePath());
  }

  /**
   * Returns true if the latestReportDirectory contains a report for the browser.
   * 
   * @param browser
   *          not NULL
   * @param latestReportDirectory
   *          not NULL
   * @throws IOException
   *           when there is a problem opening the latestReportDirectory
   * @throws ParseException
   *           when there is a problem parsing the latestReportDirectory
   */
  private boolean containsBrowser(String browser, File latestReportDirectory)
      throws IOException, ParseException {
    Preconditions.checkNotNull(browser, "browser is null");
    Preconditions.checkNotNull(latestReportDirectory, "latestReportDirectory is null");

    for (File f : latestReportDirectory.listFiles()) {
      if (f.getName().equals("execution.properties")) {
        JSONParser parser = new JSONParser();
        JSONObject a = (JSONObject) parser.parse(new FileReader(f));
        if (a.get("Name").equals(browser))
          return true;
      }
    }
    return false;
  }

  /**
   * Returns the file named JUnit_Report.xml in <code>reportDirectory</code>
   * 
   * @throws IOException
   *           if <code>reportDirectory</code> is empty or not a directory
   */
  private File getJunitReportInDirectory(File reportDirectory) throws IOException {
    if (!reportDirectory.isDirectory() || (reportDirectory.listFiles().length == 0))
      throw new IOException(reportDirectory.getPath() + "is empty or is not a directory");
    for (File possibleJUNIT : reportDirectory.listFiles()) {
      if (possibleJUNIT.getName().equals("JUnit_Report.xml")) {
        return possibleJUNIT;
      }
    }
    throw new IOException("JUnit_Report.xml could not be found in " + reportDirectory.getPath());
  }
}
