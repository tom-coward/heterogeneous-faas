using System;

public class hello
{
    public static void Main()
    {
        string line;
        for (; (line = Console.ReadLine()) != null;)
        {
            string[] s = line.Trim().Split(' ');
            Console.WriteLine((int.Parse(s[0]) + int.Parse(s[1])).ToString().Length);
        }
    }
}