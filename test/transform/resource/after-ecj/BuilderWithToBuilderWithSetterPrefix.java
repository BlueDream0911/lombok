import java.util.List;
import lombok.Builder;
@Builder(toBuilder = true,setterPrefix = "with") @lombok.experimental.Accessors(prefix = "m") class BuilderWithToBuilderWithSetterPrefix<T> {
  public static @java.lang.SuppressWarnings("all") class BuilderWithToBuilderWithSetterPrefixBuilder<T> {
    private @java.lang.SuppressWarnings("all") String one;
    private @java.lang.SuppressWarnings("all") String two;
    private @java.lang.SuppressWarnings("all") T foo;
    private @java.lang.SuppressWarnings("all") java.util.ArrayList<T> bars;
    @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> withOne(final String one) {
      this.one = one;
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> withTwo(final String two) {
      this.two = two;
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> withFoo(final T foo) {
      this.foo = foo;
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> withBar(final T bar) {
      if ((this.bars == null))
          this.bars = new java.util.ArrayList<T>();
      this.bars.add(bar);
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> withBars(final java.util.Collection<? extends T> bars) {
      if ((this.bars == null))
          this.bars = new java.util.ArrayList<T>();
      this.bars.addAll(bars);
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> clearBars() {
      if ((this.bars != null))
          this.bars.clear();
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefix<T> build() {
      java.util.List<T> bars;
      switch (((this.bars == null) ? 0 : this.bars.size())) {
      case 0 :
          bars = java.util.Collections.emptyList();
          break;
      case 1 :
          bars = java.util.Collections.singletonList(this.bars.get(0));
          break;
      default :
          bars = java.util.Collections.unmodifiableList(new java.util.ArrayList<T>(this.bars));
      }
      return new BuilderWithToBuilderWithSetterPrefix<T>(one, two, foo, bars);
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") java.lang.String toString() {
      return (((((((("BuilderWithToBuilderWithSetterPrefix.BuilderWithToBuilderWithSetterPrefixBuilder(one=" + this.one) + ", two=") + this.two) + ", foo=") + this.foo) + ", bars=") + this.bars) + ")");
    }
  }
  private String mOne;
  private String mTwo;
  private @Builder.ObtainVia(method = "rrr",isStatic = true) T foo;
  private @lombok.Singular List<T> bars;
  public static <K>K rrr(BuilderWithToBuilderWithSetterPrefix<K> x) {
    return x.foo;
  }
  @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefix(final String one, final String two, final T foo, final List<T> bars) {
    super();
    this.mOne = one;
    this.mTwo = two;
    this.foo = foo;
    this.bars = bars;
  }
  public static @java.lang.SuppressWarnings("all") <T>BuilderWithToBuilderWithSetterPrefixBuilder<T> builder() {
    return new BuilderWithToBuilderWithSetterPrefixBuilder<T>();
  }
  public @java.lang.SuppressWarnings("all") BuilderWithToBuilderWithSetterPrefixBuilder<T> toBuilder() {
    final BuilderWithToBuilderWithSetterPrefixBuilder<T> builder = new BuilderWithToBuilderWithSetterPrefixBuilder<T>().withOne(this.mOne).withTwo(this.mTwo).withFoo(BuilderWithToBuilderWithSetterPrefix.<T>rrr(this));
    if ((this.bars != null))
        builder.withBars(this.bars);
    return builder;
  }
}
@lombok.experimental.Accessors(prefix = "m") class ConstructorWithToBuilder<T> {
  public static @java.lang.SuppressWarnings("all") class ConstructorWithToBuilderBuilder<T> {
    private @java.lang.SuppressWarnings("all") String mOne;
    private @java.lang.SuppressWarnings("all") T baz;
    private @java.lang.SuppressWarnings("all") com.google.common.collect.ImmutableList<T> bars;
    @java.lang.SuppressWarnings("all") ConstructorWithToBuilderBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") ConstructorWithToBuilderBuilder<T> withMOne(final String mOne) {
      this.mOne = mOne;
      return this;
    }
    public @java.lang.SuppressWarnings("all") ConstructorWithToBuilderBuilder<T> withBaz(final T baz) {
      this.baz = baz;
      return this;
    }
    public @java.lang.SuppressWarnings("all") ConstructorWithToBuilderBuilder<T> withBars(final com.google.common.collect.ImmutableList<T> bars) {
      this.bars = bars;
      return this;
    }
    public @java.lang.SuppressWarnings("all") ConstructorWithToBuilder<T> build() {
      return new ConstructorWithToBuilder<T>(mOne, baz, bars);
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") java.lang.String toString() {
      return (((((("ConstructorWithToBuilder.ConstructorWithToBuilderBuilder(mOne=" + this.mOne) + ", baz=") + this.baz) + ", bars=") + this.bars) + ")");
    }
  }
  private String mOne;
  private String mTwo;
  private T foo;
  private @lombok.Singular com.google.common.collect.ImmutableList<T> bars;
  public @Builder(toBuilder = true) ConstructorWithToBuilder(String mOne, @Builder.ObtainVia(field = "foo") T baz, com.google.common.collect.ImmutableList<T> bars) {
    super();
  }
  public static @java.lang.SuppressWarnings("all") <T>ConstructorWithToBuilderBuilder<T> builder() {
    return new ConstructorWithToBuilderBuilder<T>();
  }
  public @java.lang.SuppressWarnings("all") ConstructorWithToBuilderBuilder<T> toBuilder() {
    return new ConstructorWithToBuilderBuilder<T>().withMOne(this.mOne).withBaz(this.foo).withBars(this.bars);
  }
}
