using System;
class p0002
{
    static void Main(string[] args)
    {
        string s;
        while ( (s = Console.ReadLine()) !=null)
        {
            var x = s.Split(' ');
            Console.WriteLine(
                (int.Parse(x[0]) + int.Parse(x[1])).ToString().Length);
        }
       
    }
}