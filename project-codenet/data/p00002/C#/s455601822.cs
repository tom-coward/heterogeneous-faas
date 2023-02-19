using System;

namespace AIZUJudge {
    class Disit {
        static void Main(string[] args) {
            while (true) {
                string data = Console.ReadLine();
                if (string.IsNullOrEmpty(data)) break;

                string[] tmp = data.Split(' ');
                int a = int.Parse(tmp[0]);
                int b = int.Parse(tmp[1]);

                int sum = a + b;
                int i = 1;
                while (true) {
                    if (sum < Math.Pow(10, i)) {
                        Console.WriteLine(i);
                        break;
                    }
                    i++;
                }
            }
       }
    }
}