# Annotator

Annotation services over Java Beans

** Annotator ** is a plain Java library targeted on delivering
an uniform reflection and annotation API over
multiple [ORM](https://en.wikipedia.org/wiki/Object%E2%80%93relational_mapping) use cases.

The API is defined in the main Interface [Annotate](src/main/java/io/github/agache41/annotator/annotator/Annotate.java).
Given a specific scope that can be a class/instance/field/method/accessors, following methods are available :

- get()                           - Gets the underlining "annotated" scope (class, field, method ..etc)
- hasAnnotation(Class clazz)      - Tells if in the current scope contains annotation of the given type.
- getAnnotation(Class annotationClass ...) - Gets the annotation present in scope of the given type parameter.
- getAnnotations()                - Gets the annotations present in scope.
- getAnnotations(Class clazz)  - Gets the annotations from the current scope of the given type.
- getAnnotationsThat(Matcher matcher)- Gets the annotations from the current scope that match the given matcher.
- getField(String name)           - Returns the field with the given name present in scope.
- getFields()                     - Returns the fields present in scope.
- getFieldsThat(Matcher matcher)  - Gets the fields from the current scope that match the given matcher.
- getMethods()                    - Returns the methods present in scope.
- getMethodsThat(Matcher matcher) - Gets the methods from the current scope that match the given matcher.
- getAccessor(String name)        - Returns the accessor with the given name present in scope.
- getAccessors()                  - Returns the accessors present in scope.
- getAccessorsThat(Matcher matcher) - Gets the accessors from the current scope that match the given matcher.

Using the annotator class through this API multiple points of interest can be queried from the ORM perspective :
classes,fields, methods, objects.
The Library also introduces the concept of accessor, an unified API over properties of a class.
The Accessor class provides uniform access methods over values stored in a Java Bean,
either using setter-getter or direct field access.
It also provides uniform access over the annotations present on the field or methods that compose this pattern.

- [Quick start](#quickstart)
    - [BaseTestClass](#baseTestClass)
    - [TestExtends](#testExtends)
    - [MarkedClass](#markedclass)
    - [Querying](#querying)
    - [Filtering](#filtering)
    - [Fields](#fields)
    - [Limitations](#limitations)
    - [Testing](#testing)
- [Accessor](#accessor)
    - [Methods](#methods)
    - [Positioning](#positioning)
    - [Embedded types](#embedded-types)
- [Demo](#demo)
- [Requirements](#requirements)
- [Installation](#installation)
- [Features](#features)
- [Structure](#structure)

## Quickstart

Let's start by creating a set of annotations and a class where we plan on using them.
The class where these annotation will be used will be named scope.
Then we will use the Annotator class to query this structure and read it.

### BaseTestClass

The annotation BaseTestClass acts as a marker interface, defining the base class used in annotation extending.
Even though inheritance does not really work for annotations, this use case is more of a marker point in identifying
specific annotations that contribute to our pattern and offers a clean method of separating them from other annotations
present in the current scope.

```java

@Retention(RetentionPolicy.RUNTIME)
public @interface BaseTestClass {
}
```

### TestExtends

The annotations @TestExtends and @TestExtendsWithValue are our target annotations.
Notice that they are annotated with @Extends, providing our marker annotation BaseTestClass.class.

```java

@Extends(BaseTestClass.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestExtends {
}


@Extends(BaseTestClass.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestExtendsWithValue {
    String value();
}

```

### MarkedClass

Now let's place the annotations on several points in the class:

```java

@Data
@TestExtends
@TestExtendsWithValue("myClassValue")
public class MarkedClass {

    @TestExtends
    private String annotatedField;

    private String notAnnotatedField;

    @TestExtendsWithValue("myValue")
    private String annotatedWithValue;

    @TestExtends
    @TestExtendsWithValue("myBothValue")
    private String annotatedWithBoth;
}
```

Notice the used @Data annotation from [Lombok](https://projectlombok.org/).

### Querying

The Annotator instance can be created on a class, instance of a class (object), field , method and accessor.
Let's create it on a new MarkedClass instance and query the present annotations.

```java
final List<Annotation> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations()
        .collect(Collectors.toList());
```

As expected the list will contain the @Data and the two @TestExtends and @TestExtendsWithValue annotations instances
present on the class.

### Filtering

But in our use case we are only interested in getting the two @TestExtends and @TestExtendsWithValue annotation.
To do this we have to filter the annotations stream.
A naive approach would be to filter by using the two class types.

```java
final List<TestExtends> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations(TestExtends.class)
        .collect(Collectors.toList());

final List<TestExtends> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotations(TestExtendsWithValue.class)
        .collect(Collectors.toList());
```

This will, however, bring us two collections that have to be merged together
and the code would be difficult to maintain when a third annotation
in the use case comes in question (Example would be a hypothetical @TestExtendsWithTwoValues)

So let's focus the filtering on the common ground of the two annotation,
that is the fact that both are being annotated with @Extends.

The annotator class provides us with already built in methods for filtering
and the much-needed Matcher and Predicate instances.

Enjoy reading the code!

```java
final List<Annotation> annotations = Annotator
        .of(new MarkedClass())
        .getAnnotationsThat(HaveAnnotation.ofType(Extends.class)
                                          .having(AnExtendsValue.of(BaseTestClass.class)))
        .collect(Collectors.toList());
```

This will " get the annotations that have an annotation of type Extends, having an extends value of BaseTestClass ".
So the two @TestExtends and @TestExtendsWithValue will be returned.

### Fields

In a very similar way we can query the class for the Fields that are annotated with one of our annotations:

```java
final List<Field> fields = Annotator
        .of(new MarkedClass())
        .getFieldsThat(HaveAnnotation.ofType(TestExtends.class))
        .collect(Collectors.toList());
```

or both

```java
final List<Field> fields = Annotator
        .of(new MarkedClass())
        .getFieldsThat(HaveAnnotation.ofType(TestExtends.class)
                                     .and(HaveAnnotation.ofType(TestExtendsWithValue.class)))
        .collect(Collectors.toList());
```

The Fields obtained from the class can be further queried:

```java
Field field;
final List<Annotation> annotations = Annotator
        .of(field)
        .getAnnotationsThat(HaveAnnotation.ofType(Extends.class)
                                          .having(AnExtendsValue.of(BaseTestClass.class)))
        .collect(Collectors.toList());
```

The Annotator.of(class/object/field/method/accessor) method acts as a central singleton instance factory
and is the only method that has to be used for implementing queries.
The instances are being created at first call and are being reused and cached for optimization reasons.

### Limitations

Querying for annotations can be done on every instance, that is because class, fields or methods can have them.
Querying for fields on a class follows the same principle. You can not however look for like fields of methods
on a field or method, an IllegalStateException will be thrown.

### Testing

Once the annotations and the using class are created, testing them can be done by querying and comparing results:
Here is an example looking for fields marked with @TestExtends annotation

```java

@Test
public void testFieldsThatHaveTestExtends() {
    final List<Field> fields = Annotator
            .of(new MarkedClass())
            .getFieldsThat(HaveAnnotation
                                   .ofType(TestExtends.class))
            .collect(Collectors.toList());

    Assertions.assertEquals(2, fields.size());
    final Field field = fields.get(0);
    Assertions.assertNotNull(field);
    Assertions.assertEquals(String.class, field.getType());
}
```

## Accessor

Accessor pattern implements uniform access to a class property, independent of the use cases
of field access or setter - getter access or any combination of them.
A class property refers to a value being stored in a class.
Starting from the standard java bean construct :

```java
public class Bean {
    private String name;

    public getName() {
        return name;
    }

    public setName(String name) {
        this.name = name;
    }
}
```

In this case the field is private and the setter and getter have to be used.
However, the following example can also be taken into account:

```java
public class Bean {
    public String name;
}
```

or this one :

```java
public class Bean {
    public String name;

    public getName() {
        return name;
    }
}
```

In the second case the getter - setter are missing, so we have to use direct field access.
And more combinations can be here discovered.

Accessor provides uniform api over all these cases and enables us
to access the class property independent of the bean implementation form.
Accessors are being created automatically by the class Annotator.
The method getAccessors() or the filtered getAccessorsThat()
enable us to use accessors exactly like fields or methods.

## Methods

Methods that access general field or method properties

- getDeclaringClass() - gets the class enclosing this accessor
- getName() - gets the field or property name
- getType() - Gets the type.
- getModifiers() - gets the modifiers
- getGenericType() - the generic type.
- getField() - gets the field.
- getGetter() - gets the getter.
- getSetter() - gets the setter.

Methods used to set or get value through this accessor

- get(Object rootObject) - Gets the value from this accessor, applying it on the given rootObject.
- getAs(Object rootObject, Class<V> expectingType) - Gets the value as the given type.
- getAs(Object rootObject, Class<V> expectingType, V defaultValue) - Gets the value as the given type or returns the
  default value.
- set(Object rootObject, Object value) - Gets the value through this accessor, applying it on the given rootObject.
- newInstance(Object rootObject) - Creates a new instance of the underlining value of this accessor,applying it on the
  given rootObject.
  Annotation methods
- getAnnotation(Class annotationClass ..) - Gets annotation of a specific type
- getAnnotations() - Gets the annotations on the underlining field or getter / setter

Positioning methods

- getLevel() - gets the level of this accessor.
- setLevel(int level) - sets the level of this accessor.
- getParent() - gets the parent of this accessor.
- hasPosition() - Tells if this field has a position.
- getPosition() - Gets the position of this accessor.
- boolean isAtRootLevel() - Tells if this field is located at root level.
- boolean isLeaf() - Tells if this field is a end of the tree, and does not expand anymore

## Positioning

One of the most common problems in ORM that the fields in java class are not ordered.
And that leads to problematic situations when that impacts the order of the execution steps.
The most naive example would be a text parser that consumes the input and fills the fields in the
orm bean in the order that the fields come from the call Class.getDeclaredFields().
Let's just add one more quote from
the [method Javadoc](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredFields--)
"The elements in the returned array are not sorted and are not in any particular order."

So the positioning of the fields in a class has to be well established.

The Position annotation does exactly that :

```java

@Inherited
@Repeatable(Positions.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Position {
    int value();
}
```

Example of usage:

```java

@Data
@NoArgsConstructor
public class AnnotatedClass {
    @Position(5)
    private final Integer field3;
    @Position(2)
    private Double field2;
    @Position(4)
    private Boolean fields4;
    @Position(3)
    private final Long field3;
    @Position(1)
    private String field1;
}
```

Notice the used @Data annotation from [Lombok](https://projectlombok.org/).
When querying for accessors int the class we can sort them :

```java
 List<Accessor<?>> accessorList = Annotator
        .of(AnnotatedClass.class)
        .getAccessors()
        .sorted(/*new PositionComparator()*/)
        .collect(Collectors.toList());
```

or we can get only the ones that have simple types (are not part of a embedded property)

```java
 List<Accessor<?>> accessorList = Annotator
        .of(AnnotatedClass.class)
        .getAccessors()
        .sorted(new PositionComparator())
        .filter(Accessor::isLeaf)
        .collect(Collectors.toList());
```

## Embedded types

The problem of positioning complicates itself further when the class has embedded properties.
Here is an example:

```java

@Data
@NoArgsConstructor
public class AnnotatedClass {
    @Position(5)
    private SubAnotatedPlaceholder r3;
    @Position(2)
    private String f2;
    @Position(4)
    private String f9;
    @Recurse
    @Position(3)
    private SubAnnotatedClass r1;
    @Position(1)
    private String f1;
}


```

The AnnotatedClass contains two embedded property classes SubAnnotatedClass and SubAnotatedPlaceholder:

```java

@Data
@NoArgsConstructor
public class SubAnnotatedClass {
    @Position(3)
    private String f7;
    @Position(4)
    private String f8;
    @Position(2)
    private SubSubAnnotatedClass r2;
    @Position(1)
    private String f3;
}

@Data
@NoArgsConstructor
@Recurse
public class SubAnotatedPlaceholder {

    @Position(0)
    private String f10;

    @Recurse
    @Position(1)
    private SubAnnotatedClass r4;

    @Position(2)
    private String f11;


}
```

SubAnnotatedClass contains a further embedded class SubSubAnnotatedClass:

```java

@Data
@NoArgsConstructor
@Recurse
public class SubSubAnnotatedClass {
    @Position(3)
    private String f5;
    @Position(4)
    private String f6;
    @Position(1)
    private String f4;
}
```

The annotation @Recurse (placed either on the class definition or at the place of use)
tells the Class Annotator to "recurse", that is to go into the embedded types
and extract and construct the Accessors for them too.
And the ordering will be also respected,
starting in the parent and continuing in the children.
For this example the ordering of the accessors is :
"f1,f2,r1.f3,r1.r2.f4,r1.r2.f5,r1.r2.f6,r1.f7,r1.f8,f9,r3.f10,r3.r4.f3,
r3.r4.r2.f4,r3.r4.r2.f5,r3.r4.r2.f6,r3.r4.f7,r3.r4.f8,r3.f11"

Sounds complicated ? Annotator makes it easy for you. Just use @Position annotation and you will be fine.

```java

```

## Demo

//:todo
А comprehensive example of using the library with JPA database you can find in the **[demo](/demo)** module.

## Requirements

The library works with Java 8+.

## Installation

Simply add  `io.github.agache41:annotator` dependency to your project.

```xml

<dependency>
    <groupId>io.github.agache41</groupId>
    <artifactId>annotator</artifactId>
    <version>the version to use</version>
</dependency>
```

## Features

- Easy to install, just add it to the classpath.
- Easy to use, just compose your queries using the predicates.

## Structure

The library is packaged as a single jar.

## Dependencies

```xml

<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

Testing dependencies are listed here. Please note the Lombok is used only in the test context.

```xml

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.9.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.24</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
