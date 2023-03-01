package com.cst438.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.GradebookDTO;

@RestController
public class InstructorController {

	@Autowired
	AssignmentRepository assignmentRepository;
	
	@GetMapping("/assignment/{id}")
	public AssignmentListDTO changeAssignmentName( @PathVariable("id") Integer assignmentId) {
		String email = "dwisneski@csumb.edu";
		Assignment assignment = checkAssignment(assignmentId, email);
		
		AssignmentListDTO  al = new AssignmentListDTO ();
		al.assignmentId= assignmentId;
		al.assignmentName = assignment.getName();
		
		if (al != null) {
				al.assignmentName = assignment.getName();
		} 
		//al.add(grade);
		
		
		return al;
	}
	
	
	@GetMapping("/assignment/{id}/")
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
