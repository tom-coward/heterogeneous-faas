using System;

namespace Prob0002
{
    class Program
    {
        static void Main(string[] args)
        {
            // EOFまで1行ずつ読み込む
            string line;
            while((line = Console.ReadLine()) != null)
            {
                string[] tokens = line.Split(' ');
                int sum = int.Parse(tokens[0]) + int.Parse(tokens[1]);

                // 桁数数え上げ
                int digit = 1;
                sum /= 10;
                while(sum != 0)
                {
                    digit++;
                    sum /= 10;
                }

                Console.WriteLine(digit);
            }
        }
    }
}

