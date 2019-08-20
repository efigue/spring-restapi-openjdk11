package api.businessobjects;

public class Person {
    private Integer id;
    private String name;
    private Integer age;
    private String occupation;

    public Person(){ }

    public Person(Integer id, String name, Integer age, String occupation){
        this.id = id;
        this.name = name;
        this.age = age;
        this.occupation = occupation;
    }

    public Integer getId() { return id; }

    public void setID(Integer id) { this.id = id; }

    public Integer getAge() { return age; }

    public void setAge(Integer age) { this.age = age; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getOccupation() { return occupation; }

    public void setOccupation(String occupation) { this.occupation = occupation; }

    @Override
    public String toString(){
        return String.format(
                "Person{\n" +
                        "\tID: %s,\n" +
                        "\tname: '%s',\n" +
                        "\tage: %s,\n" +
                        "\toccupation '%s'\n" +
                "}\n"
        ,id,name,age,occupation);
    }
}