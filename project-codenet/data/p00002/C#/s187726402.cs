using System;
using System.Collections.Generic;
using System.Linq;

class Program
{
    static void Main()
    {
        List<int> sums = new List<int>();
        
        while (true)
        {
            try
            {
                int[] input = Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse);
                sums.Add(input[0] + input[1]);
                
            }
            catch
            {
                break;
                
            }
            
        }
        var Sums_Length = sums.Select(a => a.ToString().Length);
        foreach (var sum_length in Sums_Length)
        {
            Console.WriteLine(sum_length);
        }
            
    }
}
