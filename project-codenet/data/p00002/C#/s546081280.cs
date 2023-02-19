public class Hello{
    public static void Main(){
        string line = System.Console.ReadLine();
        while(line != null)
        {
            string[] num = line.Split(' ');
            int a = int.Parse(num[0]);
            int b = int.Parse(num[1]);
            System.Console.WriteLine((a + b).ToString().Length);
            line = System.Console.ReadLine();
        }
    }
}