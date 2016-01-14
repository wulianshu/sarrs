package com.chaojishipin.sarrs.manager;


import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.chaojishipin.sarrs.bean.DateTag;
import com.chaojishipin.sarrs.bean.Favorite;
import com.chaojishipin.sarrs.dao.FavoriteDao;
import java.util.List;

/**
 * 顶部toast展现辅助类
 * Created by xll on 2015/10/08.
 */
public class FavoriteManager {

   static FavoriteManager instanse;
   static  FavoriteDao    fdao;

    public synchronized  static final FavoriteManager getInstanse(){

        if(instanse==null){
            instanse=new FavoriteManager();
        }
        fdao=new FavoriteDao(ChaoJiShiPinApplication.getInstatnce());
        return instanse;

    }
    /**
     *  添加一条收藏
     * */
    public int save(Favorite f){
      int iret=-1;
        if(fdao!=null){

            fdao.save(f);
            iret=0;
        }
        return iret;

    }
    /**
     *  批量收藏
     * */
    public int saveBatch(List<Favorite> fs){
        int iret=-1;
        if(fdao!=null){

            fdao.saveBatch(fs);
            iret=0;
        }
        return iret;

    }

    /**
     *  判断是否存在此专辑
     *  @param aid
     * */

   public boolean isExistsByAid(String aid){
       boolean iret=false;
       if(fdao!=null){

           iret=fdao.isExistsAid(aid);
       }
       return iret;
   }

    /**
     *  判断是否存在此单视频
     *  @param gvid
     * */

    public boolean isExistsByGvid(String gvid){
        boolean iret=false;
        if(fdao!=null){

            iret=fdao.isExistsGvid(gvid);
        }
        return iret;
    }

    /**
     *  判断是否存在此专题
     *  @param tid
     * */

    public boolean isExistsByTid(String tid){
        boolean iret=false;
        if(fdao!=null){

            iret=fdao.isExistsTid(tid);
        }
        return iret;
    }

    /**
     *   delete by aid
     * */

   public int deletByAid(String aid){
       int iret=-1;
       if(fdao!=null){

           fdao.delete(aid);
           iret=0;
       }
       return iret;

   }
    /**
     *   delete by id
     * */

    public int deletById(String id){
        int iret=-1;
        if(fdao!=null){

            fdao.deleteById(id);
            iret=0;
        }
        return iret;

    }

    /**
     *   delete by tid
     * */

    public int deletByTid(String tid){
        int iret=-1;
        if(fdao!=null){

            fdao.deleteByTid(tid);
            iret=0;
        }
        return iret;

    }

    /**
     *   delete by Gvid
     * */

    public int deletByGvid(String gvid){
        int iret=-1;
        if(fdao!=null){

            fdao.deleteByGvid(gvid);
            iret=0;
        }
        return iret;

    }
/**
 *  更新收藏
 * */
    public int update(Favorite f){

        int iret=-1;
        if(fdao!=null){

            fdao.update(f);
            iret=0;
        }
        return iret;
    }
/**
 *  分页查询所有收藏记录
 *  */
   public List<Favorite> queryAll(int pageIndex,int pageSize){
       List<Favorite> fs=null;

       if(fdao!=null){

          fs= fdao.getAll(pageIndex,pageSize);
       }
       return fs;

   }
    /**
     *  查询所有收藏记录
     *  */
    public List<Favorite> queryAll(){
        List<Favorite> fs=null;

        if(fdao!=null){

            fs= fdao.getAll();
        }
        return fs;

    }


    /**
     *  查询distinctDate
     *  */
    public List<DateTag> queryDistinctDate(){
        List<DateTag> fs=null;

        if(fdao!=null){

            fs= fdao.getDistinctDate();
        }
        return fs;

    }

    /**
     *  查询所有Date
     *  */
    public List<String> queryListDate(){
        List<String> fs=null;

        if(fdao!=null){

            fs= fdao.getListDate();
        }
        return fs;

    }


    /**
   *  批量删除
   * */

   public int deletePatch(List<Favorite> list){
       int iret=-1;
       if(fdao!=null){

           fdao.deletePatch(list);
           iret=0;
       }
       return iret;


   }








}
