using System;
using System.Collections.Generic;

namespace P0002_DigitNumber
{
    class Program
    {
        static void Main(string[] args)
        {
            List<string> lines = new List<string>();
            string readLine;
            
            do
            {
                readLine = Console.ReadLine();
                if (readLine != null) 
                {
                    lines.Add(readLine);
                }
            }
            while (readLine != null);
            

            foreach (string line in lines)
            {
                string[] digits = line.Split(' ');

                int add = int.Parse(digits[0]) + int.Parse(digits[1]);
                
                Console.WriteLine(add.ToString().Length);

            }

        }
        
    }
}