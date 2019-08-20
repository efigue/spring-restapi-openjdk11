package api.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
interface ApiController <T> {

    //GET
    List<T> index();
    T show(@PathVariable String id);

    //POST
    List<T> search(@RequestBody Map<String, String> body);
    T create(@RequestBody Map<String, String> body);

    //PUT
    T update(@PathVariable String id, @RequestBody Map<String, String> body);

    //DELETE
    boolean delete(@PathVariable String id);
}
