package com.saket.javastreams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.saket.javastreams.MainActivity.Gender.*;

/**
 * Stream is a sequence of elements.
 * <p>
 * The java.util.Stream interface provides a sequence of elements and
 * a set of methods that support aggregate operations (foreach, filter etc.) on these elements.
 * <p>
 * The java collections framework (java.util.collection) provides a method stream()
 * which allows to convert any of its children (List, Set, Queue, Map etc) to be converted into
 * a stream instance.
 */
public class MainActivity extends AppCompatActivity {

    static class Member implements Comparable<Member> {
        private final String name;
        private final Gender gender;
        private String starSing;
        private final int age;
        private List<String> favoriteColors;

        Member(String name, Gender gender, int age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }

        public void setStarSign(String starSing) {
            this.starSing = starSing;
        }

        public String getStarSign() {
            return starSing;
        }

        public List<String> getFavoriteColors() {
            return favoriteColors;
        }

        public void setFavoriteColors(List<String> favoriteColors) {
            this.favoriteColors = favoriteColors;
        }

        @Override
        public int compareTo(Member o) {
            return name.compareTo(o.name);
        }
    }

    enum Gender {
        MALE, FEMALE
    }

    /*
    List — an ordered collection (sometimes called a sequence).
    Lists can contain duplicate elements.
    The user of a List generally has precise control over where in the list each element
    is inserted and can access elements by their integer index (position).
     */
    List<Member> family;

    /*
    Set — a collection that cannot contain duplicate elements.
    Sets cannot contain 2 elements e1 and e2 where e1.equals(e2)
    At most 1 null element.
    Care must be taken if having mutable elements in the set, so that they
    do not fail the not equals condition of a set.
     */
    Set<Member> familySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //I think CopyOnWriteArraySet is used for concurrent tasks
        //familySet = new CopyOnWriteArraySet<>();
        //Empty set does not support addAll()
        //familySet = Collections.emptySet();
        //All members of Tree set should implement Comparable interface
        familySet = new TreeSet<>();

        //Member vishket = new Member("Vishket", MALE, 32);
        family = Arrays.asList(
                new Member("Saket", MALE, 36),
                new Member("Komal", FEMALE, 31),
                new Member("Daddy", MALE, 70),
                new Member("Mummy", FEMALE, 61),
                new Member("Bunny", MALE, 3),
                new Member("Aniket", MALE, 35),
                new Member("Vishket", MALE, 32),
                new Member("Vishket", MALE, 32),
                new Member("Vishket", MALE, 32));


        /*
            Here we will see that although List contains duplicate members, which we add to the Set.
            But when the Set streams data, it only shows unique values..
         */
/*
        familySet.addAll(family);

        familySet.stream()
                .forEach(member -> System.out.println("Family member name " + member.name));
*/

        //Print all items in the list
        //iterateStreams();

        //Print names of only female members of the family
        //filterStream();

        //Print star signs for each family member
        //mapStream();
        //Display favorite colors of each family member
        //flatmapStreams();

        //Match names of family
        //matchStreams();

        //Get sum of age of family
        reduceStream();

        //Print family member names as a list
        //collectStream();
    }

    //Iterate through stream
    //ForEach is a terminal operation that performs action for each item of the stream.
    //for parallel stream it does not guarantee to respect the encounter order of the stream.
    private void iterateStreams() {
        family.stream()
                .distinct() //Note distinct only compares references. So two new members with same name will still be considered as distinct objects...
                .forEach(person -> System.out.println("Name : " + person.name));
    }

    //Filter a stream.
    //Filter is an intermediate operation that takes a predicate and returns elements which satisfy the condition.
    private void filterStream() {
        //Filter all women in the family
        family.stream()
                .filter(member -> FEMALE.equals(member.gender))
                .forEach(member -> System.out.println("Name : " + member.name));
    }

    //Maps - apply a function to each element of the stream before returning them..
    //Maps can change the type of element that are returned in the stream. However in this
    //example i am returning the same type as the input type. Again, this is an intermediate operation.
    private void mapStream() {
        //First i define an imaginary function which returns the star sign for each member based on some logic...
        Function<Member, Member> getStarSignFunction = member -> {
            switch (member.name) {
                case "Saket":
                    member.setStarSign("Cancer");
                    break;
                case "Komal":
                    member.setStarSign("Scorpio");
                    break;
                case "Bunny":
                    member.setStarSign("Virgo");
                    break;
                case "Daddy":
                    member.setStarSign("Sagittarius");
                    break;
                case "Mummy":
                    member.setStarSign("Libra");
                    break;
                case "Vishket":
                    member.setStarSign("Aquarius");
                    break;
                case "Aniket":
                    member.setStarSign("Leo");
            }
            return member;
        };
        //Then we pass this function to the map operator which returns the star sign and we finally print it using foreach terminal operator
        family.stream().map(getStarSignFunction)
                .forEach(member -> System.out.println("Name : " + member.name + ", Star sign - " + member.getStarSign()));
    }


    /*
    Flat maps are similar to maps. But they are useful in case where each element in a sequence
    has its own sequence of elements. And if you wish to return only elements from the child element then you
    use the flatmap. Basically you flatten the map here.
     */
    private void flatmapStreams() {
        //Add favorite colors for family members
        family.stream()
                .forEach(member -> member.setFavoriteColors(Arrays.asList("Red", "Blue", "Yellow", "Green")));

        //Now each family member has its own list of fav colors. We want to print the members and their favorite colors
        family.stream()
                .flatMap(member -> {
                    System.out.println("Member Name - " + member.name);
                    return member.getFavoriteColors().stream();
                })
                .forEach(color -> System.out.println("Favorite Color - " + color));
    }

    //Matching - streams provides 3 different matchers -
    //anyMatch, allMatch and noneMatch. Each takes a predicate which returns a boolean which indicates if the element satisfy the predicate..
    private void matchStreams() {
        //Here we check if any member of the family has name with letter a
        final boolean any_contains_a = family.stream()
                .anyMatch(member -> member.name.contains("a"));
        System.out.println(any_contains_a ? "Family member contains name with a" : "No family member contains name with a");

        //Here we check if all members of the family has name with letter a
        final boolean all_contains_a = family.stream()
                .allMatch(member -> member.name.contains("a"));
        System.out.println(all_contains_a ? "All family member contains name with a" : "Not all family members contains name with a");

        //Here we check if none of members of the family has name with letter a
        final boolean none_contains_a = family.stream()
                .noneMatch(member -> member.name.contains("a"));
        System.out.println(none_contains_a ? "No family member contains name with a" : "Some family members contains name with a");
    }

    //Reduction - can be used to reducing sequence of elements to a value according a given function.
    private void reduceStream() {
        //A Binary operator is a bi.function that represents a operation between 2 operands
        // of same type and return is also same type.
        //For this sample we provide sum of age of 2 family members.
        BinaryOperator<Integer> binaryOperator = (age1, age2) -> age1 + age2;

        Optional<Integer> reduced = family.stream()
                .map(member -> member.age)
                .reduce(binaryOperator);

        System.out.println("Reduced result - " + reduced.get());
    }

    //Collection - another way to achieve reduction is by converting stream to a collection or a hash
    private void collectStream() {
        List<Member> memberList = family.stream().collect(Collectors.toList());
        memberList.forEach(member -> System.out.println(member.name));
    }

}