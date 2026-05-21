import { NavLink, Outlet } from "react-router-dom";

export const Layout = () => {
    const navClass = ({ isActive }) =>
        `rounded-md px-3 py-2 text-sm font-semibold transition ${
            isActive ? "bg-white text-sky-800 shadow-sm" : "text-sky-50 hover:bg-sky-500/35"
        }`;

    const items = [
        { to: "/", label: "Рейсы", end: true },
        { to: "/operations", label: "Пассажирские операции" },
        { to: "/models", label: "Модели" },
        { to: "/aircrafts", label: "Самолеты" },
        { to: "/passengers", label: "Пассажиры" },
        { to: "/employees", label: "Сотрудники" },
        { to: "/airports", label: "Аэропорты" },
        { to: "/airlines", label: "Авиакомпании" },
    ];

    return (
        <div className="min-h-screen bg-[#eef6fb]">
            <nav className="sticky top-0 z-40 bg-sky-700 px-6 py-3 shadow-sm">
              <div className="mx-auto flex max-w-7xl flex-wrap items-center gap-2">
                <div className="mr-5 text-base font-bold text-white">ИС Аэропорта</div>
                {items.map((item) => (
                    <NavLink key={item.to} to={item.to} end={item.end} className={navClass}>
                        {item.label}
                    </NavLink>
                ))}
              </div>
            </nav>

            <main>
                <Outlet />
            </main>
        </div>
    );
};
