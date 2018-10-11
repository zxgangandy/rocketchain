package com.rocketchain.utils;

public abstract class Either<A, B> {
    abstract public boolean isLeft();

    abstract public boolean isRight();

    abstract public A left();

    abstract public B right();


    public static class Left<A> extends Either<A, Nothing> {
        private A value;

        public Left (A value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public A left() {
            return value;
        }

        @Override
        public Nothing right() {
            return null;
        }
    }

    public static class Right<B> extends Either<Nothing, B> {
        private B value;

        public Right (B value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public Nothing left() {
            return null;
        }

        @Override
        public B right() {
            return value;
        }
    }
}
