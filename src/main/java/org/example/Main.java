package org.example;

public class Main {
    public static void main(String[] args) throws Throwable {
        Parent p = new Parent();
        p.callMethod();

        Child c = new Child();
        c.callMethod();
    }

    public static class Parent {
        private final A a;

        public Parent() {
            a = new A() {
                @Override
                public void foo() {
                    bar();
                }
            };
        }

        public void callMethod() {
            a.foo();
        }

        protected void bar() {
            System.out.println("Parent#foo");
        }
    }

    public static class Child extends Parent {
        @Override
        protected void bar() {
            System.out.println("Child#foo");
        }
    }

    private abstract static class A {
        public abstract void foo();
    }
}
