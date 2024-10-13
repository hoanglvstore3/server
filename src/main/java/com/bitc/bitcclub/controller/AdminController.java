package com.bitc.bitcclub.controller;

import com.bitc.bitcclub.model.*;
import com.bitc.bitcclub.repository.BitcClassRepository;
import com.bitc.bitcclub.repository.CoursesRepository;
import com.bitc.bitcclub.repository.PersonRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    BitcClassRepository bitcClassRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    CoursesRepository coursesRepository;

    @RequestMapping("/displayClasses")
    public ModelAndView displayClasses(Model model) {
        List<BitcClass> bitcClasses = bitcClassRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("classes.html");
        modelAndView.addObject("bitcClasses",bitcClasses);
        modelAndView.addObject("bitcClass", new BitcClass());
        return modelAndView;
    }

    @PostMapping("/addNewClass")
    public ModelAndView addNewClass(Model model, @ModelAttribute("bitcClass") BitcClass bitcClass) {
        bitcClassRepository.save(bitcClass);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @RequestMapping("/deleteClass")
    public ModelAndView deleteClass(Model model, @RequestParam int id) {
        Optional<BitcClass> bitcClass = bitcClassRepository.findById(id);
        for(Person person : bitcClass.get().getPersons()){
            person.setBitcClass(null);
            personRepository.save(person);
        }
        bitcClassRepository.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @GetMapping("/displayStudents")
    public ModelAndView displayStudents(Model model, @RequestParam int classId, HttpSession session,
                                        @RequestParam(value = "error", required = false) String error) {
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("students.html");
        Optional<BitcClass> bitcClass = bitcClassRepository.findById(classId);
        modelAndView.addObject("bitcClass",bitcClass.get());
        modelAndView.addObject("person",new Person());
        session.setAttribute("bitcClass",bitcClass.get());
        if(error != null) {
            errorMessage = "Invalid Email entered!!";
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/addStudent")
    public ModelAndView addStudent(Model model, @ModelAttribute("person") Person person, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        BitcClass bitcClass = (BitcClass) session.getAttribute("bitcClass");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if(personEntity==null || !(personEntity.getPersonId()>0)){
            modelAndView.setViewName("redirect:/admin/displayStudents?classId="+bitcClass.getClassId()
                    +"&error=true");
            return modelAndView;
        }
        personEntity.setBitcClass(bitcClass);
        personRepository.save(personEntity);
        bitcClass.getPersons().add(personEntity);
        bitcClassRepository.save(bitcClass);
        modelAndView.setViewName("redirect:/admin/displayStudents?classId="+bitcClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/deleteStudent")
    public ModelAndView deleteStudent(Model model, @RequestParam int personId, HttpSession session) {
        BitcClass bitcClass = (BitcClass) session.getAttribute("bitcClass");
        Optional<Person> person = personRepository.findById(personId);
        person.get().setBitcClass(null);
        bitcClass.getPersons().remove(person.get());
        BitcClass bitcClassSaved = bitcClassRepository.save(bitcClass);
        session.setAttribute("bitcClass",bitcClassSaved);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayStudents?classId="+bitcClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/displayCourses")
    public ModelAndView displayCourses(Model model) {
        List<Courses> courses = coursesRepository.findAll(Sort.by("name").descending());
        ModelAndView modelAndView = new ModelAndView("courses_secure.html");
        modelAndView.addObject("courses",courses);
        modelAndView.addObject("course", new Courses());
        return modelAndView;
    }

    @PostMapping("/addNewCourse")
    public ModelAndView addNewCourse(Model model, @ModelAttribute("course") Courses course) {
        ModelAndView modelAndView = new ModelAndView();
        coursesRepository.save(course);
        modelAndView.setViewName("redirect:/admin/displayCourses");
        return modelAndView;
    }

    @GetMapping("/viewStudents")
    public ModelAndView viewStudents(Model model, @RequestParam int id
                 ,HttpSession session,@RequestParam(required = false) String error) {
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("course_students.html");
        Optional<Courses> courses = coursesRepository.findById(id);
        modelAndView.addObject("courses",courses.get());
        modelAndView.addObject("person",new Person());
        session.setAttribute("courses",courses.get());
        System.out.println(courses.get().getName());

        if(error != null) {
            errorMessage = "Invalid Email entered!!";
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/addStudentToCourse")
    public ModelAndView addStudentToCourse(Model model, @ModelAttribute("person") Person person,
                                           HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Courses courses = (Courses) session.getAttribute("courses");
        Person personEntity = personRepository.readByEmail(person.getEmail());

        if(personEntity == null || !(personEntity.getPersonId() > 0)){
            modelAndView.setViewName("redirect:/admin/viewStudents?id=" + courses.getCourseId()
                    + "&error=true");
            return modelAndView;
        }

        if(courses.getPersons().contains(personEntity)) {
            modelAndView.setViewName("redirect:/admin/viewStudents?id=" + courses.getCourseId()
                    + "&error=alreadyExists");
            return modelAndView;
        }

         personRepository.addStudentToCourse(personEntity.getPersonId(), courses.getCourseId());




        courses.getPersons().add(personEntity);
        personRepository.save(personEntity);
        session.setAttribute("courses", courses);

        modelAndView.setViewName("redirect:/admin/viewStudents?id=" + courses.getCourseId());
        return modelAndView;
    }



    @GetMapping("/deleteStudentFromCourse")
    public ModelAndView deleteStudentFromCourse(Model model, @RequestParam int personId,
                                                HttpSession session) {
        Courses courses = (Courses) session.getAttribute("courses");
        Optional<Person> person = personRepository.findById(personId);
        personRepository.removeStudentFromCourse(person.get().getPersonId(),courses.getCourseId());

        courses.getPersons().remove(person);
        personRepository.save(person.get());
        session.setAttribute("courses",courses);
        ModelAndView modelAndView = new
                ModelAndView("redirect:/admin/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }

}
