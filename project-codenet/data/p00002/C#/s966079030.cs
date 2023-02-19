using System;

namespace ConsoleApplication1{
    class Program{
        static void Main(string[] args){
            while (true) {
                String str=Console.ReadLine();
                if(str==null){
                    break;
                }
                String[] a = str.Split();
                Console.WriteLine((int.Parse(a[0]) + int.Parse(a[1])).ToString().Length);
            }
        }
    }
}