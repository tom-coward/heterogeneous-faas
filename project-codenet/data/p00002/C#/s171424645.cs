using System;
using System.Linq;
using System.Collections.Generic;
using System.IO;

namespace AOJV0
{
    class P0002
    {
        static void Main()
        {
            foreach(var s in EnumerateInputs()
                            .Select(x => x.Split(' '))
                            .Select(x => x.Select(int.Parse))
                            .Select(x => new {X = x.First(), Y = x.Skip(1).First()}))
                Console.WriteLine((s.X + s.Y).ToString().Length);
        }

        static IEnumerable<string> EnumerateInputs()
        {
            while(true)
            {
                var s = Console.In.ReadLine();
                if(s == null)
                    break;
                yield return s;
            }
        }
    }
}