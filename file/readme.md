#### 文件下载
##### 1、Java 从服务器下载文件并保存到本地
当我们想要下载网站上的某个资源时，我们会获取一个url，它是服务器定位资源的一个描述，下载的过程有如下几步：   
（1）客户端发起一个url请求，获取连接对象。   
（2）服务器解析url，并且将指定的资源返回一个输入流给客户。   
（3）建立存储的目录以及保存的文件名。  
（4）输出了写数据。  
（5）关闭输入流和输出流。  
 代码示例：DownloadImageUtil.download

##### 2、Content-Disposition
[具体介绍见rfc2616章节](http://www.rfc-editor.org/rfc/rfc2616.pdf)   
Content-disposition 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，文件直接在浏览器上显示或者在访问时弹出文件下载对话框。

格式说明： content-disposition = "Content-Disposition" ":" disposition-type *( ";" disposition-parm )
````
disposition := "Content-Disposition" ":"  
               disposition-type  
               *(";" disposition-parm)  
disposition-type := "inline"  
                  / "attachment"  
                  / extension-token  
                  ; values are not case-sensitive  
disposition-parm := filename-parm / parameter  
filename-parm := "filename" "=" value; 
````    
Content-Disposition属性有两种类型：inline 和 attachment   
* inline ：将文件内容直接显示在页面 
* attachment：弹出对话框让用户下载   
具体例子： 
```http request
Content-Type: image/jpeg  
Content-Disposition: inline;filename=hello.jpg  
Content-Description: just a small picture of me  
```  
在页面内打开代码：   
```
File file = new File("rfc1806.txt");  
String filename = file.getName();  
response.setHeader("Content-Type","text/plain");  
response.addHeader("Content-Disposition","inline;filename=" + new String(filename.getBytes(),"utf-8"));  
response.addHeader("Content-Length","" + file.length()); 
```
弹出保存框代码：
```
File file = new File("rfc1806.txt");  
String filename = file.getName();  
response.setHeader("Content-Type","text/plain");  
response.addHeader("Content-Disposition","attachment;filename=" + new String(filename.getBytes(),"utf-8"));  
response.addHeader("Content-Length","" + file.length()); 
```

说明：Content-Disposition为属性名disposition-type是以什么方式下载，如attachment为以附件方式下载disposition-parm为默认保存时的文件名   
服务端向客户端游览器发送文件时，   
如果是浏览器支持的文件类型，一般会默认使用浏览器打开，比如txt、jpg等，会直接在浏览器中显示，   
如果需要提示用户保存，就要利用Content-Disposition进行一下处理，关键在于一定要加上attachment   

备注：这样浏览器会提示保存还是打开，即使选择打开，也会使用相关联的程序比如记事本打开，而不是IE直接打开了。  

那么由上可知具体的例子： Content-Disposition: attachment; filename="filename.xls"当然filename参数可以包含路径信息，但User-Agnet会忽略掉这些信息，只会把路径信息的最后一部分做为文件名。  
当你在响应类型为application/octet- stream情况下使用了这个头信息的话，那就意味着你不想直接显示内容，而是弹出一个"文件下载"的对话框，接下来就是由你来决定"打开"还是"保存" 了。  

注意事项：  
1.当代码里面使用Content-Disposition来确保浏览器弹出下载对话框的时候。 response.addHeader("Content-Disposition","attachment");一定要确保没有做过关于禁止浏览器缓存的操作。
代码如下:  
response.setHeader("Pragma", "No-cache");  
response.setHeader("Cache-Control", "No-cache");  
response.setDateHeader("Expires", 0);  
不然会发现下载功能在opera和firefox里面好好的没问题，在IE下面就是不行。

##### 3、使用h5 标签 href='url' download 下载踩过的坑
用户点击下载多媒体文件(图片/视频等)，最简单的方式：
```
<a href='url' download="filename.ext">下载</a>
```
如果url指向同源资源，是正常的。   
如果url指向第三方资源，download会失效，表现和不使用download时一致——浏览器能打开的文件，浏览器会直接打开，不能打开的文件，会直接下载。浏览器打开的文件，可以手动下载。   
解决方案
* 一：将文件打包为.zip/.rar等浏览器不能打开的文件下载
* 二：通过后端转发，后端请求第三方资源，返回给前端，前端使用file-saver等工具保存文件。

如果url指向的第三方资源配置了CORS，download依然无效，但可以通过xhr请求获取文件，然后下载到本地。   
```ecmascript 6

/**
 * 用FileSave保存文件
 * @param url
 */
export function downloadUrlFile(url) {
  const xhr = new XMLHttpRequest();
  xhr.open('GET', url, true);
  xhr.responseType = 'blob';
  xhr.setRequestHeader('Authorization', 'Basic a2VybWl0Omtlcm1pdA==');
  xhr.onload = () => {
    if (xhr.status === 200) {
      // 获取图片blob数据并保存
      saveAs(xhr.response, 'abc.jpg');
    }
  };
 
  xhr.send();
}
 
/**
 * URL方式保存文件到本地
 * @param name 文件名
 * @param data 文件的数据
 */
function save(name, data) {
  var urlObject = window.URL || window.webkitURL || window;
  var export_blob = new Blob([data]);
  var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a')
  save_link.href = urlObject.createObjectURL(export_blob);
  save_link.download = name;
  _fakeClick(save_link);
}
```  

第三方跨域多媒体资源无法直接下载。很奇怪，浏览器不能打开的文件可以下载，浏览器能打开的文件不能下载，这个限制似乎没有多大意义。

不依靠后端，有两个可能破解这个限制的思路。

1、window.open(url)，再向新窗口写入一个<a href='url' downlad></a>，触发点击。  
验证结果：这种向别人的网页中嵌入自己内容的方式，极大影响浏览器的安全，无法实现。  
2、<img src='url'  οnlοad='onload'>, onload的回调中，将img 绘入 canvas，canvas.toDataUrl()，然后保存。  
验证结果：canvas.drawImage(img,0,0)后，canvas被跨域资源污染，canvas.toDataUrl()调用报错。
```ecmascript 6

/**
   * 下载url图片
   * @param imageUrl
   */
  const downloadUrl = (imageUrl) => {
    const downloadCanvas = $('#download-canvas')[0];
    const img = new Image();
    img.onload = () => {
      const ctx = downloadCanvas.getContext('2d');
      ctx.drawImage(img, 0, 0);
      const imageDataUrl = downloadCanvas.toDataURL('image/jpeg'); 
      // Uncaught DOMException: Failed to execute 'toDataURL' on 'HTMLCanvasElement': Tainted canvases may not be exported.
      saveAs(imageDataUrl, '附件');
    };
    img.src = imageUrl;
  };
```
结论：

浏览器已经限制死跨域下载多媒体文件的各种方式。

最正规的办法还是让后端做一次转发。请求后端，后端向第三方请求文件，返回给前端，前端保存文件。  

   

