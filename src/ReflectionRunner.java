import java.lang.reflect.Method;

public class ReflectionRunner {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ReflectionRunner <ClassName> <MethodName> [args...]");
            return;
        }

        try {
            System.out.println("Reflection Runner running");
            // Read the class and method names from arguments
            String className = args[0];
            String methodName = args[1];

            // Get the remaining arguments (if any)
            String[] methodArgs = new String[args.length - 2];
            System.arraycopy(args, 2, methodArgs, 0, methodArgs.length);

            // Load the class
            Class<?> clazz = Class.forName(className);

            // Find the method (this example assumes no arguments or String arguments)
            Method method;
            Object result;
            if (methodArgs.length == 0) {
                method = clazz.getMethod(methodName);
                result = method.invoke(null); // Static method
            } else {
                method = clazz.getMethod(methodName, String.class);
                result = method.invoke(null, (Object) methodArgs[0]); // Pass single String argument
            }

            if (result != null) {
                System.out.println("Method result: " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
