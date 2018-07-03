import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.Builder;
final @Builder @Value class ConstructorsWithBuilderDefaults {
  public static @java.lang.SuppressWarnings("all") class ConstructorsWithBuilderDefaultsBuilder {
    private @java.lang.SuppressWarnings("all") int x;
    private @java.lang.SuppressWarnings("all") boolean x$set;
    @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaultsBuilder() {
      super();
    }
    public @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaultsBuilder x(final int x) {
      this.x = x;
      x$set = true;
      return this;
    }
    public @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaults build() {
      return new ConstructorsWithBuilderDefaults((x$set ? x : ConstructorsWithBuilderDefaults.$default$x()));
    }
    public @java.lang.Override @java.lang.SuppressWarnings("all") java.lang.String toString() {
      return (("ConstructorsWithBuilderDefaults.ConstructorsWithBuilderDefaultsBuilder(x=" + this.x) + ")");
    }
  }
  private final @Builder.Default int x;
  private static @java.lang.SuppressWarnings("all") int $default$x() {
    return 5;
  }
  @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaults(final int x) {
    super();
    this.x = x;
  }
  public static @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaultsBuilder builder() {
    return new ConstructorsWithBuilderDefaultsBuilder();
  }
  public @java.lang.SuppressWarnings("all") int getX() {
    return this.x;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof ConstructorsWithBuilderDefaults)))
        return false;
    final ConstructorsWithBuilderDefaults other = (ConstructorsWithBuilderDefaults) o;
    if ((this.getX() != other.getX()))
        return false;
    return true;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = ((result * PRIME) + this.getX());
    return result;
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") java.lang.String toString() {
    return (("ConstructorsWithBuilderDefaults(x=" + this.getX()) + ")");
  }
  private @java.lang.SuppressWarnings("all") ConstructorsWithBuilderDefaults() {
    super();
    this.x = ConstructorsWithBuilderDefaults.$default$x();
  }
}