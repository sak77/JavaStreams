package com.saket.javastreams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        //iterate_streams();

        //Print names of only female members of the family
        //filter_stream();

        //Print star signs for each family member
        //map_stream();
        //Display favorite colors of each family member
        //flatmap_streams();

        //Match names of family
        //match_streams();

        //Get sum of age of family
        //reduce_stream();

        //Print family member names as a list
        collect_stream();
    }

    class Member implements Comparable<Member> {
        private final String name;
        private final Gender gender;
        private String star_sing;
        private final int age;
        private List<String> favorite_colors;

        Member(String name, Gender gender, int age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }

        public void setStar_sing(String star_sing) {
            this.star_sing = star_sing;
        }

        public String getStar_sing() {
            return star_sing;
        }

        public List<String> getFavorite_colors() {
            return favorite_colors;
        }

        public void setFavorite_colors(List<String> favorite_colors) {
            this.favorite_colors = favorite_colors;
        }

        @Override
        public int compareTo(Member o) {
            return name.compareTo(o.name);
        }
    }

    enum Gender {
        MALE, FEMALE
    }

    //Creating Streams
    private Stream<Member> streamify() {
        return family.stream();
    }

    //Iterate through stream
    //ForEach is a terminal operation that performs action for each item of the stream.
    //for parallel stream it does not guarantee to respect the encounter order of the stream.
    private void iterate_streams() {
        streamify()
                .distinct() //Note distinct only compares references. So two new members with same name will still be considered as distinct obejcts...
                .forEach(person -> System.out.println("Name : " + person.name));
    }

    //Filter a stream.
    //Filter is an intermediate operation that takes a predicate and returns elements which satisfy the condition.
    private void filter_stream() {
        //Filter all women in the family
        streamify()
                .filter(member -> FEMALE.equals(member.gender))
                .forEach(member -> System.out.println("Name : " + member.name));
    }

    //Maps - apply a function to each element of the stream before returning them..
    //Maps can change the type of element that are returned in the stream. However in this
    //example i am returning the same type as the input type. Again, this is an intermediate operation.
    private void map_stream() {
        //First i define an imaginary function which returns the star sign for each member based on some logic...
        Function<Member, Member> getStarSignFunction = member -> {
            switch (member.name) {
                case "Saket":
                    member.setStar_sing("Cancer");
                    break;
                case "Komal":
                    member.setStar_sing("Scorpio");
                    break;
                case "Bunny":
                    member.setStar_sing("Virgo");
                    break;
                case "Daddy":
                    member.setStar_sing("Sagittarius");
                    break;
                case "Mummy":
                    member.setStar_sing("Libra");
                    break;
                case "Vishket":
                    member.setStar_sing("Aquarius");
                    break;
                case "Aniket":
                    member.setStar_sing("Leo");
            }
            return member;
        };
        //Then we pass this function to the map operator which returns the star sign and we finally print it using foreach terminal operator
        streamify().map(getStarSignFunction)
                .forEach(member -> System.out.println("Name : " + member.name + ", Star sign - " + member.getStar_sing()));
    }


    /*
    Flat maps are similar to maps. But they are useful in case where each element in a sequence
    has its own sequence of elements. And if you wish to return only elements from the child element then you
    use the flatmap. Basically you flatten the map here.
     */
    private void flatmap_streams() {
        //Add favorite colors for family members
        family.stream()
                .forEach(member -> member.setFavorite_colors(Arrays.asList("Red", "Blue", "Yellow", "Green")));

        //Now each family member has its own list of fav colors. We want to print the members and their favorite colors
        streamify()
                .flatMap(member -> {
                    System.out.println("Member Name - " + member.name);
                    return member.getFavorite_colors().stream();
                })
                .forEach(color -> System.out.println("Favorite Color - " + color));

    }

    //Matching - streams provides 3 different matchers -
    //anyMatch, allMatch and noneMatch. Each takes a predicate which returns a boolean which indicates if the element satisfy the predicate..
    private void match_streams() {
        //Here we check if any member of the family has name with letter a
        final boolean any_contains_a = streamify()
                .anyMatch(member -> member.name.contains("a"));
        System.out.println(any_contains_a ? "Family member contains name with a" : "No family member contains name with a");

        //Here we check if all members of the family has name with letter a
        final boolean all_contains_a = streamify()
                .allMatch(member -> member.name.contains("a"));
        System.out.println(all_contains_a ? "All family member contains name with a" : "Not all family members contains name with a");

        //Here we check if none of members of the family has name with letter a
        final boolean none_contains_a = streamify()
                .noneMatch(member -> member.name.contains("a"));
        System.out.println(none_contains_a ? "No family member contains name with a" : "Some family members contains name with a");
    }

    //Reduction - can be used to reducing sequence of elements to a value according a given function.
    private void reduce_stream() {
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
    private void collect_stream() {
        List<Member> memberList = streamify().collect(Collectors.toList());
        memberList.forEach(member -> System.out.println(member.name));
    }

}