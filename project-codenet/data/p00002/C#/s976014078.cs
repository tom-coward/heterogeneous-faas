using System;

class Program
{
    static void Main(string[] args)
    {
        string line;
        while((line = Console.ReadLine()) != null)
        {
            var input = line.Split(' ');
            int sum = int.Parse(input[0]) + int.Parse(input[1]);
            Console.WriteLine(sum.ToString().Length);
        }
    }
}