import java.util.List;
import lombok.experimental.Builder;

class BuilderComplex {
	@Builder
	private static <T extends Number> void testVoidWithGenerics(T number, int arg2, String arg3, BuilderComplex selfRef) {}
}
