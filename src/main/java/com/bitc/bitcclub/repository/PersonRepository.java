package com.bitc.bitcclub.repository;

import com.bitc.bitcclub.model.Courses;
import com.bitc.bitcclub.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Person readByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO person_courses (person_id, course_id) VALUES (:personId, :courseId)", nativeQuery = true)
    void addStudentToCourse(Integer personId, Integer courseId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM person_courses WHERE person_id = :personId AND course_id = :courseId", nativeQuery = true)
    void removeStudentFromCourse(@Param("personId") Integer personId, @Param("courseId") Integer courseId);


    @Query("SELECT c FROM Courses c JOIN c.persons p WHERE p.personId = :personId")
    ArrayList<Courses> findCoursesByPersonId(@Param("personId") int personId);







}
