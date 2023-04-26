
package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

public class EndToEndTestAddAssignment {
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_mac64/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE = "cst432-test";
	public static final int TEST_ID = 9999;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Test
	public void addAssignmentTest() throws Exception {
		
		
		// set the driver location and start driver
				//@formatter:off
				// browser	property name 				Java Driver Class
				// edge 	webdriver.edge.driver 		EdgeDriver
				// FireFox 	webdriver.firefox.driver 	FirefoxDriver
				// IE 		webdriver.ie.driver 		InternetExplorerDriver
				//@formatter:on
				
				/*
				 * initialize the WebDriver and get the home page. 
				 */

				System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
				WebDriver driver = new ChromeDriver();
				// Puts an Implicit wait for 10 seconds before throwing exception
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

				driver.get(URL);
				Thread.sleep(SLEEP_DURATION);
				
				
				
				AssignmentGrade x = null;
				do {
					x = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(TEST_ID, TEST_USER_EMAIL);
					if (x != null)
						assignnmentGradeRepository.delete(x);
				} while (x != null);
		   

		        try {
					
		        	driver.get(URL);
					Thread.sleep(SLEEP_DURATION);

					// select the Button Group Button
					
					WebElement we = driver.findElement(By.xpath("(//input[@type='ButtonGroup'])[last()]"));
					we.click();

					// Locate and click "Add Assignment" button
					
					driver.findElement(By.xpath("//a")).click();
					Thread.sleep(SLEEP_DURATION);

					// enter Assignment name, Course Title, and due Date and click Add button
					
					driver.findElement(By.xpath("//input[@name='Assignment']")).sendKeys(TEST_ASSIGNMENT_NAME);
					driver.findElement(By.xpath("//input[@name='Course']")).sendKeys(TEST_COURSE);
					driver.findElement(By.xpath("//input[@name='Due Date']")).sendKeys(null);
					driver.findElement(By.xpath("//button[@id='Add']")).click();
					Thread.sleep(SLEEP_DURATION);

					/*
					* verify that assignment is in assignmentgradeRepository
					* get the title of assignments in gradebook
					*/ 
				
					Assignment assignment = assignmentRepository.findById(TEST_ID).get();
					
					List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='title']/div[@class='MuiDataGrid-cellContent']"));
					boolean found = false;
					for (WebElement e : elements) {
						System.out.println(e.getText()); // for debug
						if (e.getText().equals(assignment.getName())) {
							found=true;
							break;
						}
					}
					assertTrue( found, "Assignment added but not listed in gradebook.");
					
					// verify that enrollment row has been inserted to database.
					
					AssignmentGrade ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(TEST_ID, TEST_USER_EMAIL);
					assertNotNull(ag, "Assignment not found in database.");

				}catch (Exception ex) {
					throw ex;
				} finally {

					/*
					 *  clean up database so the test is repeatable.
					 */
					AssignmentGrade ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(TEST_ID, TEST_USER_EMAIL);
					if (ag!=null) assignnmentGradeRepository.delete(ag);
		

					driver.quit();
				}
	}

}
