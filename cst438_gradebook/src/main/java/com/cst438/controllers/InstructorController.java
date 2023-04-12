package com.cst438.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentListDTO.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.Enrollment;
import com.cst438.domain.GradebookDTO;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"})
@RestController
public class InstructorController {

	@Autowired
	AssignmentRepository assignmentRepository;
	
	@PostMapping("/schedule")
	@Transactional
	public AssignmentDTO addAssignment(@RequestBody int assignmentId, int courseId, String assignmentName, String dueDate,
			String courseTitle) {
		AssignmentDTO newAssignment = new AssignmentDTO(assignmentId, courseId, assignmentName, dueDate, courseTitle);
		newAssignment.assignmentId = assignmentId;
		newAssignment.assignmentName = assignmentName;
		newAssignment.dueDate = dueDate;
		newAssignment.courseTitle = courseTitle;
		return newAssignment;
	}
	
	@PutMapping("/assignment/{id}")
	public void changeAssignmentName(@RequestBody AssignmentListDTO assignment, @PathVariable("id") Integer assignmentId) {
		String email = "dwisneski@csumb.edu";
		checkAssignment(assignmentId, email);
		
		for (AssignmentListDTO.AssignmentDTO a : assignment.assignments) {
			System.out.printf("%s\n", a.toString());
			Assignment al = assignmentRepository.findById(a.assignmentId).orElse(null);
			if (al == null) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid grade primary key. "+ a.assignmentId);
			}
			al.setName(a.assignmentName);
			System.out.printf("%s\n", al.toString());
			
			assignmentRepository.save(al);
		}
		
	}
	
	
	@DeleteMapping("/assignment/{id}/")
	public  Assignment deleteAssignment( @PathVariable("id") Integer assignmentId ) {
		String email = "dwisneski@csumb.edu";
		Assignment assignment = checkAssignment(assignmentId, email);
		if(assignment != null) {
			return assignment;
		}else {
			assignment = null;
			return assignment;
		}
		
	}
	
		
		private Assignment checkAssignment(int assignmentId, String email) {
			// get assignment 
			Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
			if (assignment == null) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment not found. "+assignmentId );
			}
			// check that user is the course instructor
			if (!assignment.getCourse().getInstructor().equals(email)) {
				throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
			}
			
			return assignment;
		}

}
