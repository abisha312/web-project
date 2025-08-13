using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using SmartAlerts.API.Interfaces;
using SmartAlerts.API.IOModels;

namespace SmartAlerts.API.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UserAPIController : ControllerBase
    {
        /*// GET: UserAPIController
        public ActionResult Index()
        {
            return View();
        }

        // GET: UserAPIController/Details/5
        public ActionResult Details(int id)
        {
            return View();
        }

        // GET: UserAPIController/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: UserAPIController/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create(IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: UserAPIController/Edit/5
        public ActionResult Edit(int id)
        {
            return View();
        }

        // POST: UserAPIController/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: UserAPIController/Delete/5
        public ActionResult Delete(int id)
        {
            return View();
        }

        // POST: UserAPIController/Delete/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Delete(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }*/
        IUserOperation _userOperation;
        public UserAPIController(IUserOperation useroperation)
        {
            _userOperation = useroperation;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="userId"></param>
        /// <returns></returns>
        [HttpGet("GetProfile")]
        public async Task<IActionResult>GetProfile(long userId)
        {
            var result = await _userOperation.GetProfile(userId);
            return Ok(result);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="request"></param>
        /// <returns></returns>
        [HttpPost("UpdateProfile")]
        public async Task<IActionResult>UpdateProfile(UserModel request)
        {
            var result = await _userOperation.UpdateProfile(request);
            return Ok(result);
        }
    }
}
