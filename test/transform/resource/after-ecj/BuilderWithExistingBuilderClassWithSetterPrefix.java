import lombok.Builder;
class BuilderWithExistingBuilderClassWithSetterPrefix<T, K extends Number> {
  public static class BuilderWithExistingBuilderClassBuilderWithSetterPrefixBuilder<Z extends Number> {
    private @java.lang.SuppressWarnings("all") boolean arg2;
    private @java.lang.SuppressWarnings("all") String arg3;
    private Z arg1;
    public void withArg2(boolean arg) {
    }
    @java.lang.SuppressWarnings("all") BuilderWithExistingBuilderClassWithSetterPrefixBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") BuilderWithExistingBuilderClassWithSetterPrefixBuilder<Z> withArg1(final Z arg1) {
      this.arg1 = arg1;
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithExistingBuilderClassWithSetterPrefixBuilder<Z> withArg3(final String arg3) {
      this.arg3 = arg3;
      return this;
    }
    public @java.lang.SuppressWarnings("all") BuilderWithExistingBuilderClassWithSetterPrefix<String, Z> build() {
      return BuilderWithExistingBuilderClassWithSetterPrefix.<Z>staticMethod(arg1, arg2, arg3);
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") java.lang.String toString() {
      return (((((("BuilderWithExistingBuilderClassWithSetterPrefix.BuilderWithExistingBuilderClassWithSetterPrefixBuilder(arg1=" + this.arg1) + ", arg2=") + this.arg2) + ", arg3=") + this.arg3) + ")");
    }
  }
  BuilderWithExistingBuilderClassWithSetterPrefix() {
    super();
  }
  public static @Builder(setterPrefix = "with") <Z extends Number>BuilderWithExistingBuilderClassWithSetterPrefix<String, Z> staticMethod(Z arg1, boolean arg2, String arg3) {
    return null;
  }
  public static @java.lang.SuppressWarnings("all") <Z extends Number>BuilderWithExistingBuilderClassWithSetterPrefixBuilder<Z> builder() {
    return new BuilderWithExistingBuilderClassWithSetterPrefixBuilder<Z>();
  }
}
