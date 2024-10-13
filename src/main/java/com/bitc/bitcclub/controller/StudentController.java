package com.bitc.bitcclub.controller;

import com.bitc.bitcclub.model.Courses;
import com.bitc.bitcclub.model.Person;
import com.bitc.bitcclub.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("student")
public class StudentController {
    @Autowired
    PersonRepository personRepository;

    @GetMapping("/displayCourses")
    public ModelAndView displayCourses(Model model, HttpSession session) {

        Person person = (Person) session.getAttribute("loggedInPerson");
        ModelAndView modelAndView = new ModelAndView("courses_enrolled.html");
        System.out.println(person.getPersonId());

        ArrayList<Courses> courses = personRepository.findCoursesByPersonId(person.getPersonId());
        for (Courses course : courses) {
            System.out.println("Course ID: " + course.getCourseId() + ", Course Name: " + course.getName());
        }


        modelAndView.addObject("person", person);
        modelAndView.addObject("courses", courses); // You can add the Set to your model if needed
        return modelAndView;
    }
}
