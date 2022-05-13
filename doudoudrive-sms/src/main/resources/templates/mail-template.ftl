<#-- 邮件通用内容模板父模板 -->
<div style="width: 100%; background: #EEEEEE; padding-top: 100px;">
    <div style="position: relative; font-family: 'Microsoft YaHei','微软雅黑',serif; width: 600px;
    background-color: #fff; margin: 0 auto; border-radius: 7px; -moz-border-radius: 7px; -webkit-border-radius: 7px; overflow: hidden;">

        <div style="position:relative; overflow: hidden; background: #212B39; color: white; padding: 22px;">
            <a style="position:relative; float: left; width: 210px; height: 42px;" href="https://www.doudoudrive.com"
               rel="noopener" target="_blank">
                <div style="position: absolute; padding-left: 4px; font-family: 'Microsoft YaHei','微软雅黑',serif;
                 fill: #FFFFFF;font-size: 32px; color: #12C0FA; line-height: 38px;">
                    兜兜网盘
                </div>
            </a>
        </div>

        <div style="color: #202020; padding: 30px 36px; box-sizing: border-box; -moz-box-sizing: border-box;-webkit-box-sizing: border-box;">
            <p style="font-size: 18px; margin: 0 0 14px;"></p>
            <div class="row">
                <#-- 子模板内容 -->
                <#include "${subTemplate}"><#t>
            </div>
        </div>
    </div>
    <p class="copy"
       style="margin-top: 50px; padding-bottom: 30px; font-size: 14px; color: #aaaaaa; text-align: center;"></p>
</div>
