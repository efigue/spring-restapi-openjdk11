package api.controllers;

import api.businessobjects.Person;
import api.data.PeopleMockData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PersonController implements ApiController <Person> {

    private PeopleMockData peopleMockData = PeopleMockData.getInstance();

    @GetMapping("/people")
    @PreAuthorize("hasRole('DevServerAdmin')")
    public List<Person> index(){
        return peopleMockData.fetchPeople();
    }

    @GetMapping("/people/{id}")
    public Person show(@PathVariable String id){
        return peopleMockData.getBlogById(Integer.parseInt(id));
    }

    @PostMapping("/people/search")
    public List<Person> search(@RequestBody Map<String, String> body){
        return peopleMockData.searchPeople(body.get("name"));
    }

    @PostMapping("/people")
    public Person create(@RequestBody Map<String, String> body){
        return peopleMockData.createPerson(
                Integer.parseInt(body.get("id")),
                body.get("name"),
                Integer.parseInt(body.get("age")),
                body.get("occupation")
        );
    }

    @PutMapping("/people/{id}")
    public Person update(@PathVariable String id, @RequestBody Map<String, String> body){
        return peopleMockData.updatePerson(
            Integer.parseInt(body.get("id")),
            body.get("name"),
            Integer.parseInt(body.get("age"))
        );
    }

    @DeleteMapping("/people/{id}")
    public boolean delete(@PathVariable String id){
        return peopleMockData.delete(Integer.parseInt(id));
    }
}