using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace SmartAlerts.API.Models;

public partial class SmartAlertsDbContext : DbContext
{
    public SmartAlertsDbContext()
    {
    }

    public SmartAlertsDbContext(DbContextOptions<SmartAlertsDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<CustomerDetail> CustomerDetails { get; set; }

    public virtual DbSet<CustomerReview> CustomerReviews { get; set; }

    public virtual DbSet<NotificationSetting> NotificationSettings { get; set; }

    public virtual DbSet<ShopDeal> ShopDeals { get; set; }

    public virtual DbSet<ShopDetail> ShopDetails { get; set; }

    public virtual DbSet<ShopType> ShopTypes { get; set; }

    public virtual DbSet<UserMaster> UserMasters { get; set; }

    public virtual DbSet<UserTypeMaster> UserTypeMasters { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseSqlServer("Name=SmartAlertsCS");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<CustomerDetail>(entity =>
        {
            entity.HasKey(e => e.CustomerDetailID).HasName("pk_CustomerDetail_CustomerDetailID");

            entity.ToTable("CustomerDetail");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.DeviceFCMToken).IsUnicode(false);
            entity.Property(e => e.DeviceType)
                .HasMaxLength(7)
                .IsUnicode(false);
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.TimeZone)
                .HasMaxLength(10)
                .IsUnicode(false);

            entity.HasOne(d => d.User).WithMany(p => p.CustomerDetails)
                .HasForeignKey(d => d.UserID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_CustomerDetail_UserID");
        });

        modelBuilder.Entity<CustomerReview>(entity =>
        {
            entity.HasKey(e => e.ReviewID).HasName("pk_CustomerReview_ReviewID");

            entity.ToTable("CustomerReview");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.Review)
                .HasMaxLength(40)
                .IsUnicode(false);

            entity.HasOne(d => d.CustomerDetail).WithMany(p => p.CustomerReviews)
                .HasForeignKey(d => d.CustomerDetailID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_CustomerReview_CustomerDetailID");

            entity.HasOne(d => d.Shop).WithMany(p => p.CustomerReviews)
                .HasForeignKey(d => d.ShopID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_CustomerReview_ShopID");
        });

        modelBuilder.Entity<NotificationSetting>(entity =>
        {
            entity.HasKey(e => e.NotificationID).HasName("pk_NotificationSetting_NotificationID");

            entity.ToTable("NotificationSetting");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.NotificationExpiration).HasColumnType("datetime");

            entity.HasOne(d => d.ShopType).WithMany(p => p.NotificationSettings)
                .HasForeignKey(d => d.ShopTypeID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_NotificationSetting_ShopTypeID");
        });

        modelBuilder.Entity<ShopDeal>(entity =>
        {
            entity.HasKey(e => e.ShopDealID).HasName("pk_ShopDeal_ShopDealID");

            entity.ToTable("ShopDeal");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.DealEndDate).HasColumnType("datetime");
            entity.Property(e => e.DealName)
                .HasMaxLength(40)
                .IsUnicode(false);
            entity.Property(e => e.DealStartDate).HasColumnType("datetime");
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.ShopLocation)
                .HasMaxLength(500)
                .IsUnicode(false);

            entity.HasOne(d => d.Shop).WithMany(p => p.ShopDeals)
                .HasForeignKey(d => d.ShopID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_ShopDeal_ShopShopID");

            entity.HasOne(d => d.ShopType).WithMany(p => p.ShopDeals)
                .HasForeignKey(d => d.ShopTypeID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_ShopDeal_ShopTypeID");
        });

        modelBuilder.Entity<ShopDetail>(entity =>
        {
            entity.HasKey(e => e.ShopDetailID).HasName("pk_ShopDetail_ShopID");

            entity.ToTable("ShopDetail");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.Latitude)
                .HasMaxLength(50)
                .IsUnicode(false);
            entity.Property(e => e.Longitude)
                .HasMaxLength(50)
                .IsUnicode(false);
            entity.Property(e => e.ShopLocation)
                .HasMaxLength(100)
                .IsUnicode(false);
            entity.Property(e => e.ShopManagerName).HasMaxLength(60);
            entity.Property(e => e.ShopName)
                .HasMaxLength(20)
                .IsUnicode(false);

            entity.HasOne(d => d.ShopType).WithMany(p => p.ShopDetails)
                .HasForeignKey(d => d.ShopTypeID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_ShopDetail_ShopTypeID");

            entity.HasOne(d => d.User).WithMany(p => p.ShopDetails)
                .HasForeignKey(d => d.UserID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_ShopDetail_UserID");
        });

        modelBuilder.Entity<ShopType>(entity =>
        {
            entity.HasKey(e => e.ShopTypeID).HasName("pk_ShopType_ShopTypeID");

            entity.ToTable("ShopType");

            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.ShopTypeName)
                .HasMaxLength(15)
                .IsUnicode(false);
        });

        modelBuilder.Entity<UserMaster>(entity =>
        {
            entity.HasKey(e => e.UserID).HasName("pk_UserMaster_UserID");

            entity.ToTable("UserMaster");

            entity.Property(e => e.Address1).HasMaxLength(200);
            entity.Property(e => e.Address2).HasMaxLength(200);
            entity.Property(e => e.City).HasMaxLength(100);
            entity.Property(e => e.ContactNo)
                .HasMaxLength(15)
                .IsUnicode(false);
            entity.Property(e => e.Country).HasMaxLength(100);
            entity.Property(e => e.CreatedOn).HasColumnType("datetime");
            entity.Property(e => e.Email)
                .HasMaxLength(360)
                .IsUnicode(false);
            entity.Property(e => e.FirstName).HasMaxLength(60);
            entity.Property(e => e.LastModifiedOn).HasColumnType("datetime");
            entity.Property(e => e.LastName).HasMaxLength(60);
            entity.Property(e => e.PostalCode).HasMaxLength(20);
            entity.Property(e => e.ShopName).HasMaxLength(60);
            entity.Property(e => e.StateOrProvince).HasMaxLength(100);
            entity.Property(e => e.TempPassword)
                .HasMaxLength(32)
                .IsUnicode(false);

            entity.HasOne(d => d.UserType).WithMany(p => p.UserMasters)
                .HasForeignKey(d => d.UserTypeID)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("fk_UserMaster_UserTypeID");
        });

        modelBuilder.Entity<UserTypeMaster>(entity =>
        {
            entity.HasKey(e => e.UserTypeId).HasName("pk_UserTypeMaster_UserTypeId");

            entity.ToTable("UserTypeMaster");

            entity.Property(e => e.UserType)
                .HasMaxLength(25)
                .IsUnicode(false);
            entity.Property(e => e.UserTypeDescription).HasMaxLength(1000);
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
