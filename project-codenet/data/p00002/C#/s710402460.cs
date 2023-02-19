using System;
using System.Text;

namespace AOJ
{
    class Program
    {
        static void Main(string[] args)
        {
            var result = Volume0.DigitNumber();
            System.Diagnostics.Trace.Write(result);
            Console.Write(result);
        }
    }

    public static class Volume0
    {
        public static string DigitNumber()
        {
            var result = new StringBuilder();
            var input = Console.ReadLine();
            while (!string.IsNullOrEmpty(input))
            {
                var inputs = input.Split(' ');
                result.AppendLine((int.Parse(inputs[0]) + int.Parse(inputs[1])).ToString().Length.ToString());
                input = Console.ReadLine();
            }
            return result.ToString();
        }
    }
}