using System;
using System.Linq;

namespace aizu
{
    class OnlineJudge
    {
        static void Main()
        {
            string data;
            while((data = Console.ReadLine()) != null) {
                Console.WriteLine(data.Split().Select(p => int.Parse(p)).Sum().ToString().Length);
            }
        }
    }
}