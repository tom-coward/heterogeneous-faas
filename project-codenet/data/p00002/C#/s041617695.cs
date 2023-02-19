using System;
using System.Linq;

namespace Aizu
{
    class OnlineJudge
    {
        public static void Main()
        {
            for(var i = ""; (i = Console.ReadLine()) != null; Console.WriteLine(i.Split().Select(int.Parse).Sum().ToString().Length));
        }
    }
}