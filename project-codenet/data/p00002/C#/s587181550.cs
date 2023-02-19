using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace ConsoleApplication2 {
    class Program {
        static void Main(string[] args) {

            TextReader input = Console.In;

            string str = "";

            while ((str = input.ReadLine()) != null) {
                // スペースで区切る
                string[] s = str.Split(' ');
                // 2 個でない場合
                if (s.Length != 2) {
                    continue;
                }
                int one = 0;
                int two = 0;
                if (!int.TryParse(s[0], out one)) {
                    continue;
                }
                if (!int.TryParse(s[1], out two)) {
                    continue;
                }
                Console.WriteLine((one + two).ToString().Length);
            }
        }
    }
}