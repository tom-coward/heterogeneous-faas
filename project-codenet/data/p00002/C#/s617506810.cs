using System;

public class hello
{
    public static void Main()
    {
        string line;
        for (; (line = Console.ReadLine()) != null;)
        {
            string[] s = line.Trim().Split(' ');
            var a = int.Parse(s[0]);
            var b = int.Parse(s[1]);
            Console.WriteLine((a + b).ToString().Length);
        }
    }
}