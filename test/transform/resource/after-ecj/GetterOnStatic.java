class Getter {
  static @lombok.Getter boolean foo;
  static @lombok.Getter int bar;
  <clinit>() {
  }
  Getter() {
    super();
  }
  public static @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") @lombok.Generated boolean isFoo() {
    return Getter.foo;
  }
  public static @java.lang.SuppressWarnings("all") @javax.annotation.Generated("lombok") @lombok.Generated int getBar() {
    return Getter.bar;
  }
}
