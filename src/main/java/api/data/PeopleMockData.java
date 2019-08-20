package api.data;

import api.businessobjects.Person;

import java.util.ArrayList;
import java.util.List;

public class PeopleMockData {
    private List<Person> people;
    private static PeopleMockData instance = null;

    public PeopleMockData(){
        people = new ArrayList<Person>();
        people.add(new Person(1, "Abigail", 70, "Retired"));
        people.add(new Person(2, "Billy", 8, "Booger Eater"));
        people.add(new Person(3, "Carl", 42, "Janitor"));
        people.add(new Person(4, "Dill", 52, "Pickle Farmer"));
        people.add(new Person(5, "Emanuel", 25, "Mechanic"));
    }
    
    public static PeopleMockData getInstance(){ return instance == null ? new PeopleMockData() : instance; }
    
    public List<Person> fetchPeople() { return people; }

    public Person getBlogById(int id) {
        for(Person person: people) {
            if(person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    public List<Person> searchPeople(String search) {
        List<Person> searchedBlogs = new ArrayList<Person>();
        
        for(Person person: people) {
            if(person.getName().toLowerCase().contains(search.toLowerCase())) {
                searchedBlogs.add(person);
            }
        }

        return searchedBlogs;
    }

    public Person createPerson(Integer id, String name, Integer age, String occupation) {
        Person newBlog = new Person(id, name, age, occupation);
        people.add(newBlog);
        return newBlog;
    }

    public Person updatePerson(int id, String name, Integer age) {
        for(Person person: people) {
            if(person.getId() == id) {
                int personIndex = people.indexOf(person);
                person.setName(name);
                person.setAge(age);
                people.set(personIndex, person);
                return person;
            }

        }

        return null;
    }

    public boolean delete(int id){
        int blogIndex = -1;

        for(Person person: people)
            if(person.getId() == id)
                blogIndex = people.indexOf(person);

        if(blogIndex > -1)
            people.remove(blogIndex);

        return true;
    }
}
