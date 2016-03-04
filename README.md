# NetTop
使用HttpUrlConnection进行封装的请求，支持多文件上传，图片下载
使用demo
  final TextView textView=(TextView)this.findViewById(R.id.text);
        firstRequest arequest=new firstRequest();
        BaseNetTopBusiness baseNetTopBusiness=new BaseNetTopBusiness(new NetTopListener() {
            @Override
            public void onSuccess(HttpResponse response) {
                System.out.println("成功");
                byte[] bytes=response.bytes;
                System.out.println(new String(bytes));
                textView.setText(new String(bytes));
            }

            @Override
            public void onFail() {
                System.out.println("on fail");
                textView.setText("fail");
            }

            @Override
            public void onError() {
                System.out.println("on error");
                textView.setText("error");
            }
        });
        baseNetTopBusiness.startRequest(arequest);
        
        首先新建请求对象类
        public class firstRequest implements Request
{
    public String requestUrl="http://192.168.56.1:18080/login-mobile";
    public String user="novsssse3333as";
    public String password="mengfanshan";
}
必须实现Request接口，其中user，password为上传参数key，requestUrl变量名不可以改变，表示请求网址,修饰符类型可以为public，private均可