#generate-password
This package is used to randomly generate a password with constraints and is normally used to reset someone's password.

#Example
```
public class GeneratePasswordMain {

    public static void main(String[] args) {
        final GeneratePassword.Builder builder = new GeneratePassword.Builder();
        builder.length(12);
        builder.minUpperCase(1);
        builder.minLowerCase(1);
        builder.minDigits(2);
        builder.minSpecialChars(1);
        builder.specialChars("!@#$%^&*");
        final GeneratePassword generatePassword = builder.build();
        System.out.println(generatePassword.generateARandomPassword());
        System.out.println(generatePassword.generateARandomPassword());
        System.out.println(generatePassword.generateARandomPassword());
    }

}

Output:
o^0r8pJE#&#f
xMqL1dC6#YGO
3&Y%QJp3ja6L
```
